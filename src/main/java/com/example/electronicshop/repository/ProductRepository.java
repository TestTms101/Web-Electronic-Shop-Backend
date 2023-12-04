package com.example.electronicshop.repository;

import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.models.product.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>{
    Optional<Product> findProductByIdAndState(String id, String state);
    Page<Product> findAllByStateOrderByCreatedDateDesc(String state, Pageable pageable);
    @Query(sort = "{ 'createdDate' : 1 }")
    List<Product> findAllByCategory_IdAndStateOrderByCreatedDateAsc(String catId, String state);

    @Query(value = "{ $or: [{'category' : ?0},{'category':{$in: ?1}}] ," +
            "'state' : 'enable'}")
    List<Product> findProductsByCategoryOrderByCreatedDateAsc(ObjectId id, List<ObjectId> subCat);
//    @Query(value = "{ $or: [{'category' : ?0},{'category':{$in: ?1}}] ," +
//            "    'state' : 'enable'}")
//    Page<Product> findProductsByCategoryOrderBySaleDesc(ObjectId id, List<ObjectId> subCat, Pageable pageable);
//    Page<Product> findByOrderByCreatedDateDesc(TextCriteria textCriteria, Pageable pageable);
//    Page<Product> findByOrderBySaleDesc(TextCriteria textCriteria, Pageable pageable);
//    @Query("{'discount': {$gte: ?0}}")
//    Page<Product> findByDiscountBetween(Long min, Long max, Pageable pageable);
//    @Query(sort = "{ 'createDate' : -1 }")
//    @Query("{$text: { $search: ?0,$language: \"en\" }}")
//    @Query("{name: { $regex: ?0 }}")
//    @Query("{$and:[{$or:[{name: { $regex: ?0, $options: 'si' }},{description: { $regex: ?0, $options: 'si' }}]},{'state': ?1}]}")
//    @Query("{discount: {$and:[{$gte: ?0},{$lte: ?1}]}}")
    Page<Product> findAllByIdOrNameOrDescriptionRegex(String id, String name, String descrip, Pageable pageable);
    Page<Product> findAllByIdOrNameOrDescriptionRegexAndState(String id, String name, String descrip, String state, Pageable pageable);

    List<Product> findAllBy(TextCriteria textCriteria);
    Page<Product> findProductBy(TextCriteria textCriteria, Pageable pageable);
    List<Product> findAllByCategory_IdAndState(ObjectId catId, String state);
//    List<Product> findAllByIdIsIn(List<String> productIds);
//
//    Optional<Product> findBy(String id, ObjectId option_id);
//    @Query(value = "{'id': ?0, 'options.value': ?1}")
//    Optional<Product> findByIdAndOptionsValue(String id, String value);
//    @Query(value = "{'id': ?0, 'images.id_image': ?1}")
//    Optional<Product> findByIdAndImagesId(String id, String image_id);
    Page<Product> findAllByStateOrderBySoldDesc(String state, Pageable pageable);
    @Aggregation("{ $group: { _id : $state, count: { $sum: 1 } } }")
    List<StateCountAggregate> countAllByState();
}
