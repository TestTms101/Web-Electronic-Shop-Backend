package com.example.electronicshop.utils;

import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.repository.OrderRepository;
import com.example.electronicshop.service.paypalpayment.PaymentUtils;
import com.example.electronicshop.config.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
@Getter
@Setter
public class TimeCancel implements Runnable{
    private OrderRepository orderRepository;
    private String orderId;
    private PaymentUtils paymentUtils;

    @Override
    public void run() {
        log.info("Start checking payment timeout!");
        if (!orderId.isBlank()) {
            Optional<Order> order = orderRepository.findOrderByIdAndState(orderId, Constant.ORDER_STATE_PROCESS);
            if (order.isPresent()) {
                try {
                    if (new Date(System.currentTimeMillis() - Constant.PAYMENT_TIMEOUT).after(
                            (Date) order.get().getPaymentDetail().getPaymentInfo()
                                    .get("orderDate"))) {
                        String check = paymentUtils.checkingUpdateQuantityProduct(order.get(), false);
                        log.info("Restock is " + (check == null));
                        order.get().setState(Constant.ORDER_STATE_CANCEL);
                        orderRepository.save(order.get());
                        log.info("Checking payment successful!");
                    } else log.warn("Time is remaining");
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.error("failed to save order when checking payment timeout!");
                }
            }
        } else log.error("Order id in checking payment timeout is blank!");
        log.info("Checking payment timeout end!");
    }
}
