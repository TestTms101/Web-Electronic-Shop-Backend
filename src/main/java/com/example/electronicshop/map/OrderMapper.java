package com.example.electronicshop.map;

import com.example.electronicshop.communication.response.OrderRes;
import com.example.electronicshop.models.enity.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class OrderMapper {
    public OrderRes toOrderRes (Order order) {
        return new OrderRes(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), order.getCreatedDate(), order.getLastModifiedDate());
    }
    public OrderRes toOrderRes2 (Order order) {
        BigDecimal fee = null;
        if(order.getDelivery().getDeliveryInfo().get("fee")==null)
            fee= BigDecimal.valueOf(0);
        else fee=new BigDecimal(order.getDelivery().getDeliveryInfo().get("fee").toString());
        return new OrderRes(order.getId(), order.getUser().getId(), order.getDelivery().getShipName(), order.getUser().getEmail(),order.getDelivery().getShipPhone(),
                order.getTotalProduct(), order.getTotalPrice(), fee,
                order.getTotalPrice().add(fee),
                order.getState(), order.getCreatedDate(), order.getLastModifiedDate());
    }
    public OrderRes toOrderDetailRes (Order order) {
        BigDecimal fee = null;
        if(order.getDelivery().getDeliveryInfo().get("fee")==null)
            fee= BigDecimal.valueOf(0);
        else fee=new BigDecimal(order.getDelivery().getDeliveryInfo().get("fee").toString());
        OrderRes orderRes =  new OrderRes(order.getId(), order.getUser().getId(), order.getDelivery().getShipName(),
                order.getUser().getEmail(),order.getDelivery().getShipPhone(), order.getTotalProduct(), order.getTotalPrice(),
                fee, order.getTotalPrice().add(fee),
                order.getState(), order.getCreatedDate(), order.getLastModifiedDate());
        orderRes.setItems(order.getItems().stream().map(CartMapper::toCartItemRes).collect(Collectors.toList()));
        orderRes.setPaymentType(order.getPaymentDetail().getPaymentType());
        orderRes.setDelivery(order.getDelivery());
        return orderRes;
    }
}