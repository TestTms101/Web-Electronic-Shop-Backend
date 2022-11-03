package com.example.electronicshop.models.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    private String imageId;
    private String url;
    private boolean thumbnail;
    private String color;
}
