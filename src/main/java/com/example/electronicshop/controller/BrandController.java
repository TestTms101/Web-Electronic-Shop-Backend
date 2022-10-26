package com.example.electronicshop.controller;

import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.service.BrandService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BrandController {
    private final BrandService brandService;

    @GetMapping(path = "/brands")
    public ResponseEntity<ResponseObject> findAll (){
        return brandService.findAll();
    }
}
