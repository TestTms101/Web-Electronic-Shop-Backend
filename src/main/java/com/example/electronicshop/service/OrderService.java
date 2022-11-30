package com.example.electronicshop.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
//    private final PaymentUtils paymentUtils;

    public ResponseEntity<?> findAll(String state, Pageable pageable) {
        Page<Order> orders;
        if (state.isBlank()) orders = orderRepository.findAll(pageable);
        else orders = orderRepository.findAllByState(state, pageable);
        if (orders.isEmpty()) throw new NotFoundException("Can not found any orders");
        List<OrderRes> resList = orders.stream().map(orderMapper::toOrderRes).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("true", "Get orders success", resList));
    }

//    public ResponseEntity<?> findOrderById(String id) {
//        Optional<Order> order = orderRepository.findById(id);
//        if (order.isPresent()) {
//            OrderRes orderRes = orderMapper.toOrderDetailRes(order.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject("true", "Get order success", orderRes));
//        }
//        throw new NotFoundException("Can not found order with id: " + id);
//    }
//
//    public ResponseEntity<?> findOrderById(String id, String userId) {
//        Optional<Order> order = orderRepository.findById(id);
//        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
//            OrderRes orderRes = orderMapper.toOrderDetailRes(order.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject("true", "Get order success", orderRes));
//        }
//        throw new NotFoundException("Can not found order with id: " + id);
//    }

//    public ResponseEntity<?> cancelOrder(String id, String userId) {
//        Optional<Order> order = orderRepository.findById(id);
//        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
//            if (order.get().getState().equals(Constant.ORDER_STATE_PENDING) ||
//                    order.get().getState().equals(Constant.ORDER_STATE_PROCESS) ||
//                    order.get().getState().equals(Constant.ORDER_STATE_PAID)) {
//                order.get().setState(Constant.ORDER_STATE_CANCEL);
//                if (order.get().getState().equals(Constant.ORDER_STATE_PAID)) {
//                    //refund
//                    order.get().getPaymentDetail().getPaymentInfo().put("refund", true);
//                }
//                orderRepository.save(order.get());
//                String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order.get(), false);
//                if (checkUpdateQuantityProduct == null) {
//                    return ResponseEntity.status(HttpStatus.OK).body(
//                            new ResponseObject("true", "Cancel order successfully", ""));
//                }
//            } else throw new AppException(HttpStatus.BAD_REQUEST.value(),
//                    "You cannot cancel or refund while the order is still processing!");
//        }
//        throw new NotFoundException("Can not found order with id: " + id);
//    }
}
