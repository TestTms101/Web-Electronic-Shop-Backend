package com.example.electronicshop.repository;

import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.models.enity.Comment;
import com.example.electronicshop.models.enity.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findAllByProduct_IdAndState(ObjectId productId, String state, Pageable pageable);
    Optional<Comment> findCommentByProduct_IdAndUser_Id(ObjectId productId, ObjectId userId);
    /* Page<Comment> findAllByUser_Id(String userId, Pageable pageable);*/
//    Optional<Comment> findCommentByIdAndState(String Id, String userId);
    Optional<Comment> findCommentByIdAndUser_IdAndState(ObjectId commentId, ObjectId userId,String State);
    List<Comment> findAllByState(String state);

    Page<Comment> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Comment> findAllByOrderByCreatedDateAsc(Pageable pageable);
    Page<Comment> findAllByStateOrderByCreatedDateDesc(String state, Pageable pageable);
    Page<Comment> findAllByStateOrderByCreatedDateAsc(String state, Pageable pageable);
    List<Comment> findCommentsByProduct_Id(ObjectId productId);
    List<Comment> findCommentsByProduct_IdAndState(ObjectId productId, String state);
    Long countAllBy();
    Long countByState(String state);

}