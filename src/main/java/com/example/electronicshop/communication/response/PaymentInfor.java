package com.example.electronicshop.communication.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfor {
    private String payId;
    private String payType;
    private String payToken;
    private Map<String, Object> getPaymentDetail =new HashMap<>();
}
