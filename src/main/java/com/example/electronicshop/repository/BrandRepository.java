package com.example.electronicshop.repository;

import com.example.electronicshop.models.enity.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BrandRepository extends MongoRepository<Brand, String> {
}
