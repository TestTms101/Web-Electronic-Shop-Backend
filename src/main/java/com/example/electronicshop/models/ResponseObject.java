package com.example.electronicshop.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseObject {
    private Boolean isSuccess;
    private String message;
    private Object data;
}
