package com.example.electronicshop.communication.response;

import com.example.electronicshop.models.product.ProductAttribute;
import com.example.electronicshop.models.product.ProductOption;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRes {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int discount;
    private double rate;
    private int rateCount;
    private String category;
    private String brand;
    private String state;
    private List<ProductAttribute> attr;
    private List<ProductOption> options;
//    private List<ProductImage> images;
}
