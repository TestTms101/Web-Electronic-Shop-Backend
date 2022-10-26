package com.example.electronicshop.service;

import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.Brand;
import com.example.electronicshop.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    public ResponseEntity<ResponseObject> findAll() {
        List<Brand> list = brandRepository.findAll();
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("true", "Get all brand success", list));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", "Cannot find brand ",""));

    }
}
