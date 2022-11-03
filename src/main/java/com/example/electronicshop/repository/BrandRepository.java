package com.example.electronicshop.repository;

import com.example.electronicshop.models.enity.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BrandRepository extends MongoRepository<Brand, String> {
    Optional<Brand> findBrandByIdAndState(String id, String state);

}
