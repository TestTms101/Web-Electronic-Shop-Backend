package com.example.electronicshop.repository;


import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findUserByEmailAndState(String email, String state);
    Optional<User> findUsersByEmail(String email);
    Optional<User> findUserByIdAndState(String id, String state);
    Page<User> findUsersByRole(String role, Pageable pageable);
    List<User> findUserByState(String state);
    @Query("{$or:[{email: { $regex: ?0, $options: 'si' }},{name: { $regex: ?0, $options: 'si' }},{phone: { $regex: ?0, $options: 'si' }}]}")
    Page<User> findUserBy(String string, Pageable pageable);

//    @Query("{'email': ?0, 'state': ?1}")
//    Optional<User> findEmailAndState(String email, String state);
    boolean existsByEmail(String email);

    @Aggregation("{ $group: { _id : $state, count: { $sum: 1 } } }")
    List<StateCountAggregate> countAllByState();
}
