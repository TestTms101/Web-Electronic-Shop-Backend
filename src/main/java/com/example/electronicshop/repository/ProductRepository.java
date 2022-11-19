package com.example.electronicshop.repository;

import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.models.product.ProductOption;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findProductByIdAndState(String id, String state);
    Page<Product> findAllByState(String state, Pageable pageable);
    Page<Product> findAllByCategory_IdAndState(ObjectId catId, String state, Pageable pageable);
    @Query(value = "{ $or: [{'category' : ?0},{'category':{$in: ?1}}] ," +
            "    'state' : 'enable'}")
    Page<Product> findProductsByCategory(ObjectId id, List<ObjectId> subCat, Pageable pageable);
    Page<Product> findAllBy(TextCriteria textCriteria, Pageable pageable);
//    List<Product> findAllByIdIsIn(List<String> productIds);
//
//    Optional<Product> findByIdAndIdOptions(String id, ObjectId option_id);
    @Query(value = "{'id': ?0, 'options.va': ?1}")
    Optional<Product> findByIdAndOptionsId(String id, String option_id);
    @Query(value = "{'id': ?0, 'images.id_image': ?1}")
    Optional<Product> findByIdAndImagesId(String id, String image_id);
}
