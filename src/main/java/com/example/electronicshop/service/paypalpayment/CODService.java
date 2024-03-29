package com.example.electronicshop.service.paypalpayment;

import com.example.electronicshop.config.Constant;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.repository.OrderRepository;
import com.example.electronicshop.service.MailService;
import com.example.electronicshop.utils.EmailUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CODService extends PaymentFactory{
    private PaymentUtils paymentUtils;
    private final OrderRepository orderRepository;
    private final TaskScheduler taskScheduler;
    private final MailService mailService;
    private final EmailUtils emailUtils;

    @Override
    @Transactional
    public ResponseEntity<?> createPayment(HttpServletRequest request, Order order) {
        if (order != null && order.getState().equals(Constant.ORDER_STATE_PROCESS)) {
            String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order, true);
            String checkUpdateSold =paymentUtils.setSoldProduct(order,true);
            if (checkUpdateQuantityProduct == null && checkUpdateSold==null) {
                order.setState(Constant.ORDER_STATE_PENDING);
                order.setCreatedDate(LocalDateTime.now());
                order.getPaymentDetail().getPaymentInfo().put("isPaid", false);
                orderRepository.save(order);
                emailUtils.setOrder(order);
                emailUtils.setMailService(mailService);
                taskScheduler.schedule(emailUtils, new Date(System.currentTimeMillis()));
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, " Pay by COD successfully", ""));
            }
        } throw new NotFoundException("Can not found order with id: "+ Objects.requireNonNull(order).getId());
    }

    @Override
    public ResponseEntity<?> executePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
       return null;
    }

    @Override
    @Transactional
    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getState().equals(Constant.ORDER_STATE_PENDING)) {
            order.get().setState(Constant.ORDER_STATE_CANCEL);
            orderRepository.save(order.get());
           String checkUpdateQuantityProduct = paymentUtils.checkingUpdateQuantityProduct(order.get(), false);
            String checkUpdateSold =paymentUtils.setSoldProduct(order.get(),false);
            if (checkUpdateQuantityProduct == null && checkUpdateSold==null) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, "Cancel order successfully", ""));
            }
        } throw new NotFoundException("Can not found order with id: "+ id);
    }
}
