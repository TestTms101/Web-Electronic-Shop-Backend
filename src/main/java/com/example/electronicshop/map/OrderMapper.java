package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.OrderRes;
import com.example.electronicshop.models.enity.Order;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderMapper {
    public OrderRes toOrderRes (Order order) {
        return new OrderRes(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState());
    }

//    public OrderRes toOrderDetailRes (Order order) {
//        OrderRes orderRes =  new OrderRes(order.getId(), order.getUser().getId(), order.getUser().getName(),
//                order.getTotalProduct(), order.getTotalPrice(), order.getState());
//        orderRes.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
//        orderRes.setPaymentType(order.getPaymentDetail().getPaymentType());
//        orderRes.setDeliveryDetail(order.getDeliveryDetail());
//        return orderRes;
//    }
}
