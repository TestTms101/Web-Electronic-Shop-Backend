package com.example.electronicshop.repository;


import com.example.electronicshop.models.enity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findUserByEmailAndState(String email, String state);
    Optional<User> findUserByIdAndState(String id, String state);

    @Query("{'email': ?0, 'state': ?1}")
    Optional<User> findEmailAndState(String email, String state);
    boolean existsByEmail(String email);

    Optional<User> findUserById(String id);
}
