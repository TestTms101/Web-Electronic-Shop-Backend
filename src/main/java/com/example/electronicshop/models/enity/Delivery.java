package com.example.electronicshop.models.enity;

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
public class Delivery {
    private String shipName;
    private String shipPhone;
    private String shipProvince;
    private String shipDistrict;
    private String shipWard;
    private String shipAddress;
    private Map<String, Object> deliveryInfo = new HashMap<>();

    public Delivery(String shipName, String shipPhone, String shipProvince, String shipDistrict, String shipWard, String shipAddress) {
        this.shipName = shipName;
        this.shipPhone = shipPhone;
        this.shipProvince = shipProvince;
        this.shipDistrict = shipDistrict;
        this.shipWard = shipWard;
        this.shipAddress = shipAddress;
    }
}

