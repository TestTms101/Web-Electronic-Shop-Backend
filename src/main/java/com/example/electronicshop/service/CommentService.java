package com.example.electronicshop.service;

import com.example.electronicshop.config.Constant;
import com.example.electronicshop.map.CommentMapper;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.Comment;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.communication.request.CommentReq;
import com.example.electronicshop.communication.response.CommentRes;
import com.example.electronicshop.repository.CommentRepository;
import com.example.electronicshop.repository.ProductRepository;
import com.example.electronicshop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentMapper commentMapper;

    public ResponseEntity<?> findByProductId(String productId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByProduct_IdAndEnable(new ObjectId(productId) , true, pageable);
        if (comments.isEmpty()) throw new NotFoundException("Can not found any review");
        List<CommentRes> resList = comments.getContent().stream().map(commentMapper::toReviewRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", comments.getTotalElements());
        resp.put("totalPage", comments.getTotalPages());
        if (comments.isEmpty()) throw new NotFoundException("Can not found any review");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("true", "Get review by product success ", resp));
    }

    @Transactional
    @Synchronized
    public ResponseEntity<?> addComment(String userId, CommentReq req) {
        Optional<Comment> comment = commentRepository.findReviewByProduct_IdAndUser_Id(
                new ObjectId(req.getProductId()), new ObjectId(userId));
        if (comment.isPresent()) throw new AppException(HttpStatus.CONFLICT.value(), "You already review this product");
        Optional<User> user = userRepository.findUserByIdAndState(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Product> product = productRepository.findProductByIdAndState(req.getProductId(), Constant.ENABLE);
            if (product.isPresent()) {
                Comment newReview = new Comment(req.getContent(), req.getRate(), product.get(), user.get(), true);
                commentRepository.save(newReview);
                double rate = ((product.get().getRate() * product.get().getRateCount()) + req.getRate())/ (product.get().getRateCount()+1);
                product.get().setRate(rate);
                productRepository.save(product.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("true", "Add review success ", newReview));
            } throw new NotFoundException("Can not found product with id: " + req.getProductId());
        } throw new NotFoundException("Can not found user with id: " + userId);
    }
}