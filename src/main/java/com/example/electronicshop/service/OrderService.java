package com.example.electronicshop.service;

import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.communication.response.OrderRes;
import com.example.electronicshop.config.Constant;
import com.example.electronicshop.map.OrderMapper;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.repository.OrderRepository;
import com.example.electronicshop.service.paypalpayment.PaymentUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentUtils paymentUtils;

    public ResponseEntity<?> findAll(String state, Pageable pageable) {
        Page<Order> orders;
        if (state.isBlank()) orders = orderRepository.findAll(pageable);
        else orders = orderRepository.findAllByState(state, pageable);
        if (orders.isEmpty()) throw new NotFoundException("Can not found any orders");
        List<OrderRes> resList = orders.stream().map(orderMapper::toOrderRes2).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get orders success", resp));
    }

    public ResponseEntity<?> findAllNoEnable(Pageable pageable) {
        Page<Order> orders;
        orders = orderRepository.findAllByStateNoEnable(pageable);
        if (orders.isEmpty()) throw new NotFoundException("Can not found any orders");
        List<OrderRes> resList = orders.stream().map(orderMapper::toOrderRes2).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get orders success", resp));
    }

    public ResponseEntity<?> findOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            OrderRes orderRes = orderMapper.toOrderDetailRes(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get order success", orderRes));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found order with id: " + id, ""));
    }

    public ResponseEntity<?> findOrderById(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            OrderRes orderRes = orderMapper.toOrderDetailRes(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get order success", orderRes));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found order with id: " + id, ""));
    }

    public ResponseEntity<?> searchAndSortAndFilter(String key, String from, String to, String sortBy, String state, Pageable pageable) {
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now();
        String pattern = "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        try {
            if (!from.isBlank()) fromDate = LocalDate.parse(from, df).atStartOfDay();
            if (!to.isBlank()) toDate = LocalDate.parse(to, df).atStartOfDay();
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Incorrect date format");
        }

        toDate = toDate.plusDays(1);

        Page<Order> orderList;

        if (state.equals("") || state.equals("all")) {
            if (sortBy.equals("") || sortBy.equals("lasted")) {
                orderList = orderRepository.findByIdOrDelivery_ShipNameRegexAndCreatedDateBetweenOrderByCreatedDateDesc(key, key, fromDate, toDate, pageable);
            } else {
                orderList = orderRepository.findByIdOrDelivery_ShipNameRegexAndCreatedDateBetweenOrderByCreatedDateAsc(key, key, fromDate, toDate, pageable);
            }
        } else {
            if (sortBy.equals("") || sortBy.equals("lasted")) {
                orderList = orderRepository.findByIdOrDelivery_ShipNameRegexAndCreatedDateBetweenAndStateOrderByCreatedDateDesc(key, key, fromDate, toDate, state, pageable);
            } else {
                orderList = orderRepository.findByIdOrDelivery_ShipNameRegexAndCreatedDateBetweenAndStateOrderByCreatedDateAsc(key, key, fromDate, toDate, state, pageable);
            }
        }

        if (orderList.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(false, "Can not found any order with: " + key, orderList));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", orderList.getContent());
        map.put("totalPage", orderList.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all order success", map));
    }

    public ResponseEntity<?> getAllCountOrders() {
        try {
            List<StateCountAggregate> resp = new ArrayList<>();
            resp.add(new StateCountAggregate("all", orderRepository.countByStateNotEnable()));
            resp.add(new StateCountAggregate("complete", orderRepository.countByState(Constant.ORDER_STATE_COMPLETE)));
            resp.add(new StateCountAggregate("process", orderRepository.countByState(Constant.ORDER_STATE_PROCESS)));
            resp.add(new StateCountAggregate("delivery", orderRepository.countByState(Constant.ORDER_STATE_DELIVERY)));
            resp.add(new StateCountAggregate("pending", orderRepository.countByState(Constant.ORDER_STATE_PENDING)));
            resp.add(new StateCountAggregate("pendingpay", orderRepository.countByState(Constant.ORDER_STATE_PENDINGPAY)));
            resp.add(new StateCountAggregate("cancel", orderRepository.countByState(Constant.ORDER_STATE_CANCEL)));
            resp.sort(Comparator.comparing(StateCountAggregate::getCount).reversed());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get count by Orders success", resp));
        } catch (Exception e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
    }


    public ResponseEntity<?> cancelOrder(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            if (order.get().getState().equals(Constant.ORDER_STATE_PENDING) ||
                    order.get().getState().equals(Constant.ORDER_STATE_ENABLE) ||
                    order.get().getState().equals(Constant.ORDER_STATE_PROCESS)) {
                String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order.get(), false);
                String checkUpdateSold = paymentUtils.setSoldProduct(order.get(), false);
                order.get().setState(Constant.ORDER_STATE_CANCEL);
                orderRepository.save(order.get());
                if (checkUpdateQuantityProduct == null && checkUpdateSold == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject(true, "Cancel order successfully", ""));
                }
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "You cannot cancel");
        }
        throw new NotFoundException("Can not found order with id: " + id);
    }

    public ResponseEntity<?> setDeliveryOrderByAdmin(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getState().equals(Constant.ORDER_STATE_PENDING) || order.get().getState().equals(Constant.ORDER_STATE_PENDINGPAY)) {
            order.get().setState(Constant.ORDER_STATE_DELIVERY);
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Delivery order successfully", order));
        } else throw new NotFoundException("Can not found or delivery order with id: " + id);
    }

    public ResponseEntity<?> setCompleteOrderByAdmin(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            if (order.get().getState().equals(Constant.ORDER_STATE_DELIVERY)) {
                order.get().setState(Constant.ORDER_STATE_COMPLETE);
                order.get().setLastModifiedDate(LocalDateTime.now());
                orderRepository.save(order.get());
                {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject(true, "Complete order successfully", order));
                }
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "You cannot complete this order");
        }
        throw new NotFoundException("Can not found order with id: " + id);
    }

    public ResponseEntity<?> setCancelOrderByAdmin(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            order.get().setState(Constant.ORDER_STATE_CANCEL);
            orderRepository.save(order.get());
            {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, "Cancel order successfully", order));
            }
        }
        throw new NotFoundException("Can not found order with id: " + id);
    }

    public ResponseEntity<?> findAllOrderByUserId(String userId, String state, Pageable pageable) {
        Page<Order> orders;
        if(state.equals("all")||state.equals(""))
            orders = orderRepository.findOrderByUser_Id(new ObjectId(userId), pageable);
        else orders = orderRepository.findOrderByUser_IdAndState(new ObjectId(userId),state,pageable);
        List<OrderRes> resList = orders.stream().map(orderMapper::toOrderDetailRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalOrder", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        if (resList.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get order success", resp));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any order", ""));
    }

    public ResponseEntity<?> findAllOrderCompleteByUserId(String userId, Pageable pageable) {
        Page<Order> orders = orderRepository.getOrderByUser_IdAndState(new ObjectId(userId), Constant.ORDER_STATE_COMPLETE, pageable);
        List<OrderRes> resList = orders.stream().map(orderMapper::toOrderDetailRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        if (resList.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get order success", resp));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any order", ""));
//        throw new NotFoundException("Can not found any order" );
    }
}
