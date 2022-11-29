package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.CartItemRes;
import com.example.electronicshop.communication.response.CartRes;
import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.models.enity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CartMapper {
//    public CartRes toCartRes (Order order) {
//        CartRes res = new CartRes(order.getId(), order.getTotalProduct(), order.getTotalPrice(), order.getState());
//        res.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
//        return res;
//    }
//
//    public static CartItemRes toCartItemRes(OrderItem orderItem) {
//        return new CartItemRes(orderItem.getId(), orderItem.getItem().getProduct().getName(),
//                orderItem.getItem().getProduct().getDiscount(),
//                orderItem.getItem().getVariants().get(0).getImages().get(0).getUrl(),
//                orderItem.getItem().getProduct().getPrice().add(orderItem.getItem().getExtraFee()),
//                orderItem.getItem().getId(), orderItem.getColor(), orderItem.getItem().getName(),
//                orderItem.getQuantity(), orderItem.getItem().getVariants().get(0).getStock(), orderItem.getSubPrice());
//    }
}
