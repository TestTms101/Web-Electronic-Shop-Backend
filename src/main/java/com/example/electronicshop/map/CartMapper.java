package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.CartItemRes;
import com.example.electronicshop.communication.response.CartRes;
import com.example.electronicshop.models.enity.Order;
import com.example.electronicshop.models.enity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CartMapper {
    public CartRes toCartRes (Order order) {
        CartRes res = new CartRes(order.getId(), order.getTotalProduct(), order.getTotalPrice(), order.getState());
        res.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
        return res;
    }

    public static CartItemRes toCartItemRes(OrderItem orderItem) {
        return new CartItemRes(orderItem.getId(),orderItem.getItem().getName(), orderItem.getItem().getId(),
                orderItem.getItem().getSale(),orderItem.getItem().getImages(),orderItem.getItem().getPrice(),
                orderItem.getValue(),orderItem.getQuantity(), orderItem.getItem().getOptions().get(0).getStock(),
                orderItem.getSubPrice());

    }
}
