package com.example.electronicshop.service.paypalpayment;

import com.example.electronicshop.config.Constant;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
@Service
@AllArgsConstructor
public class CODService extends PaymentFactory{
//    private PaymentUtils paymentUtils;
//    private final OrderRepository orderRepository;
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> createPayment(HttpServletRequest request, Order order) {
//        if (order != null && order.getState().equals(Constant.ORDER_STATE_PROCESS)) {
//            String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order, true);
//            if (checkUpdateQuantityProduct == null) {
//                order.setState(Constants.ORDER_STATE_PENDING);
//                order.getPaymentDetail().getPaymentInfo().put("isPaid", false);
//                orderRepository.save(order);
//                return ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseObject(true, " Pay by COD successfully", ""));
//            }
//        } throw new NotFoundException("Can not found order with id: "+ Objects.requireNonNull(order).getId());
//    }
//
//    @Override
//    public ResponseEntity<?> executePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
//        Optional<Order> order = orderRepository.findById(paymentId);
//        if (order.isPresent() && order.get().getState().equals(Constants.ORDER_STATE_PENDING)) {
//            order.get().setState(Constants.ORDER_STATE_DELIVERY);
//            orderRepository.save(order.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject(true, "Confirmed order successfully", ""));
//        } else throw new NotFoundException("Can not found order with id: "+ paymentId);
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
//        Optional<Order> order = orderRepository.findById(id);
//        if (order.isPresent() && order.get().getState().equals(Constants.ORDER_STATE_PENDING)) {
//            order.get().setState(Constants.ORDER_STATE_CANCEL);
//            orderRepository.save(order.get());
//            String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order.get(), false);
//            if (checkUpdateQuantityProduct == null) {
//                return ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseObject(true, "Cancel order successfully", ""));
//            }
//        } throw new NotFoundException("Can not found order with id: "+ id);
//    }
}
