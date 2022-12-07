package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.OrderRes;
import com.example.electronicshop.models.enity.Order;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderMapper {
    public OrderRes toOrderRes (Order order) {
        return new OrderRes(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), order.getCreatedDate(), order.getLastModifiedDate());
    }

    public OrderRes toOrderDetailRes (Order order) {
        OrderRes orderRes =  new OrderRes(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getUser().getEmail(),order.getUser().getPhone(), order.getTotalProduct(),
                order.getTotalPrice(), order.getState(), order.getCreatedDate(), order.getLastModifiedDate());
        orderRes.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
        orderRes.setPaymentType(order.getPaymentDetail().getPaymentType());
        orderRes.setDelivery(order.getDelivery());
        return orderRes;
    }
}