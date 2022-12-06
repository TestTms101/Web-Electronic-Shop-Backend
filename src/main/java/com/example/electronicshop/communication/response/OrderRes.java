package com.example.electronicshop.communication.response;

import com.example.electronicshop.models.enity.Delivery;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderRes {
    private String id;
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private long totalProduct = 0;
    private BigDecimal totalPrice;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CartItemRes> items = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String paymentType;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Delivery delivery;
    private String state;

    public OrderRes(String id, String userId, String userName, long totalProduct, BigDecimal totalPrice, String state) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.totalProduct = totalProduct;
        this.totalPrice = totalPrice;
        this.state = state;
    }

    public OrderRes(String id, String userId, String userName, String email, String phone, long totalProduct, BigDecimal totalPrice, String state) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.totalProduct = totalProduct;
        this.totalPrice = totalPrice;
        this.state = state;
    }
}
