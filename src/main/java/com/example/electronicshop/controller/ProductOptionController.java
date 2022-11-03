package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.ProductOptionReq;
import com.example.electronicshop.service.ProductOptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/manage")
public class ProductOptionController {
    private ProductOptionService productOptionService;

    @PostMapping(value = "/products/option/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addOption(@PathVariable("productId") String id,
                                       @Valid @ModelAttribute ProductOptionReq req) {
        return productOptionService.addOption(id, req);
    }

    @PutMapping(value = "/products/option/{id}")
    public ResponseEntity<?> updateOptionVariant(@PathVariable("id") String id,
                                                 @RequestParam("variantColor") String variantColor,
                                                 @Valid @RequestBody ProductOptionReq req) {
        variantColor = "#"+variantColor;
        return productOptionService.updateOptionVariant(id, variantColor, req);
    }

    @GetMapping("/option")
    public ResponseEntity<?> findOptionByProductId(@RequestParam("productId") String id) {
        return productOptionService.findOptionByProductId(id);
    }

    @GetMapping("/option/{id}")
    public ResponseEntity<?> getOptionById(@PathVariable("id") String id) {
        return productOptionService.findOptionById(id);
    }
}
