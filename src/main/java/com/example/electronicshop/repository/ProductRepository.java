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
//    @Query("{$and:[{$or:[{name: { $regex: ?0, $options: 'si' }},{description: { $regex: ?0, $options: 'si' }}]},{'state': ?1}]}")
//    @Query("{discount: {$and:[{$gte: ?0},{$lte: ?1}]}}")
    List<Product> findAllByIdOrNameOrDescriptionRegex(String id, String name, String descrip);
    List<Product> findAllByIdOrNameOrDescriptionRegexAndState(String id, String name, String descrip, String state);
    List<Product> findAllByCategory_IdAndState(ObjectId catId, String state);
//    List<Product> findAllByIdIsIn(List<String> productIds);
    Page<Product> findAllByStateOrderBySoldDesc(String state, Pageable pageable);
    @Aggregation("{ $group: { _id : $state, count: { $sum: 1 } } }")
    List<StateCountAggregate> countAllByState();
    Long countAllBy();
    Long countByState(String state);
}
