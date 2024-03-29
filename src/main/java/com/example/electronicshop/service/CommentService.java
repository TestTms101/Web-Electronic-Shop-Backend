package com.example.electronicshop.service;

import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.communication.request.CommentReq;
import com.example.electronicshop.communication.response.CommentRes;
import com.example.electronicshop.config.Constant;
import com.example.electronicshop.map.CommentMapper;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.Comment;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.repository.CommentRepository;
import com.example.electronicshop.repository.ProductRepository;
import com.example.electronicshop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentMapper commentMap;
//    private final OrderRepository orderRepository;


    public ResponseEntity<?> findByProductId(String productId, Pageable pageable) {
        Page<Comment> comment = commentRepository.findAllByProduct_IdAndState(new ObjectId(productId), Constant.COMMENT_ENABLE, pageable);
        List<CommentRes> resList = comment.getContent().stream().map(commentMap::toAllCommentRes).collect(Collectors.toList());
        return addPageableToRes(comment, resList);
//        Map<String, Object> resp = new HashMap<>();
//        resp.put("list", resList);
//        resp.put("totalQuantity", comment.getTotalElements());
//        resp.put("totalPage", comment.getTotalPages());
//        if (comment.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject(false, "Can not found any comment", resp));
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject(true, "Get comment by product success ", resp));
    }

    private ResponseEntity<?> addPageableToRes(Page<Comment> comments, List<CommentRes> resList) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", comments.getTotalElements());
        resp.put("totalPage", comments.getTotalPages());
        if (resList.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get comment success ", resp));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any comment", resp));
    }

    public ResponseEntity<?> findByProductIdAndCreateDateAndState(String productId, String sortBy, String state, Pageable pageable) {
        List<Comment> comment = switch (state) {
            case "" -> commentRepository.findCommentsByProduct_Id(new ObjectId(productId));
            default -> commentRepository.findCommentsByProduct_IdAndState(new ObjectId(productId), state);
        };
        List<CommentRes> resList = comment.stream().map(commentMap::toAllCommentRes).collect(Collectors.toList());
        if (sortBy.equals("oldest")) {
            resList.sort(Comparator.comparing(CommentRes::getCreatedDate));
        } else {
            resList.sort(Comparator.comparing(CommentRes::getCreatedDate).reversed());
        }
//        return addPageableToRes(comment,resList);
        if (!resList.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all comment success", resList));
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any comment with: " + productId, resList));
    }

    @Transactional
    @Synchronized
    public ResponseEntity<ResponseObject> addComment(String userId, CommentReq req) {
        Optional<Comment> comment = commentRepository.findCommentByProduct_IdAndUser_Id(
                new ObjectId(req.getProductId()), new ObjectId(userId));
        if (comment.isPresent())
            throw new AppException(HttpStatus.CONFLICT.value(), "You already comment this product");
        Optional<User> user = userRepository.findUserByIdAndState(userId, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            Optional<Product> product = productRepository.findProductByIdAndState(req.getProductId(), Constant.ENABLE);
            if (product.isPresent()) {
                {
                    Comment newComment = new Comment(req.getContent(), req.getRate(), product.get(), user.get(), Constant.COMMENT_ENABLE, LocalDateTime.now());
                    commentRepository.save(newComment);
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject(true, "Add comment success ", newComment));
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(false, "Can not found product with id: " + req.getProductId(), ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found user with id: " + userId, ""));
//        throw new NotFoundException("Can not found user with id: " + userId);
    }


    @Transactional
    public ResponseEntity<?> editCommentByUser(String id, String userid, CommentReq commentReq) {
        Optional<Comment> comment = commentRepository.findCommentByIdAndUser_IdAndState(new ObjectId(id), new ObjectId(userid), Constant.COMMENT_ENABLE);
        Optional<User> user = userRepository.findUserByIdAndState(userid, Constant.USER_ACTIVE);
        if (user.isPresent()) {
            if (comment.isPresent()) {
                comment.get().setContent(commentReq.getContent());
                comment.get().setRate(commentReq.getRate());
                comment.get().setLastUpdateDate(LocalDateTime.now());
                commentRepository.save(comment.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, "Update comment successfully", comment)
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(false, "Cannot edit comment ", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found user with id: " + userid, ""));
    }

    public ResponseEntity<ResponseObject> blockComment(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            comment.get().setState(Constant.COMMENT_BLOCK);
            commentRepository.save(comment.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Block comment successfully ", comment)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found comment with id: " + id, ""));
//        throw new NotFoundException("Can not found comment with id: "+id);
    }

    public ResponseEntity<ResponseObject> setEnableComment(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            comment.get().setState(Constant.COMMENT_ENABLE);
            commentRepository.save(comment.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, " Comment successfully ", comment)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found comment with id: " + id, ""));
//        throw new NotFoundException("Can not found comment with id: "+id);
    }

    public ResponseEntity<ResponseObject> findAllComment(String sortBy, String state, Pageable pageable) {
        Page<Comment> comments;

        if (state.equals("") || state.equals("all")) {
            if (sortBy.equals("lasted") || sortBy.equals("")) {
                comments = commentRepository.findAllByOrderByCreatedDateDesc(pageable);
            } else {
                comments = commentRepository.findAllByOrderByCreatedDateAsc(pageable);
            }
        } else {
            if (sortBy.equals("lasted") || sortBy.equals("")) {
                comments = commentRepository.findAllByStateOrderByCreatedDateDesc(state, pageable);
            } else {
                comments = commentRepository.findAllByStateOrderByCreatedDateAsc(state, pageable);
            }
        }

        Map<String, Object> map = new HashMap<>();

        if (comments.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(false, "Can not found any comment", map));
        }

        List<CommentRes> commentRes = comments.getContent().stream().map(commentMap::toAllCommentRes).toList();

        map.put("list", commentRes);
        map.put("totalPage", comments.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all comment success", map));
    }

    public ResponseEntity<?> getAllCountComments() {
        try {
            List<StateCountAggregate> resp = new ArrayList<>();
//            resp = userRepository.countAllByState();
            resp.add(new StateCountAggregate("all", commentRepository.countAllBy()));
            resp.add(new StateCountAggregate("enable", commentRepository.countByState(Constant.COMMENT_ENABLE)));
            resp.add(new StateCountAggregate("block", commentRepository.countByState(Constant.COMMENT_BLOCK)));
            resp.sort(Comparator.comparing(StateCountAggregate::getCount).reversed());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get count by Comments success", resp));
        } catch (Exception e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<ResponseObject> deleteCommemt(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            try {
                commentRepository.deleteById(comment.get().getId());

            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Error when delete comment with id: " + id);
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Delete comment successfully ", "")
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found comment with id: " + id, ""));
//        throw new NotFoundException("Can not found comment with id: "+id);
    }

    @Transactional
    public ResponseEntity<ResponseObject> deleteCommemtbyUser(String id, String userid) {
        Optional<User> user = userRepository.findUserByIdAndState(userid, Constant.USER_ACTIVE);
        if (user.isPresent()) {
//            Optional<Comment> comment = commentRepository.findById(id);
            Optional<Comment> comment = commentRepository.findCommentByIdAndUser_IdAndState(new ObjectId(id), new ObjectId(userid), Constant.COMMENT_ENABLE);
            if (comment.isPresent()) {
                try {
                    commentRepository.deleteById(comment.get().getId());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new NotFoundException("Error when delete comment with id: " + id);
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, "Delete comment successfully ", "")
                );
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found user with id: " + id, ""));
//        throw new NotFoundException("Can not found user with id: "+id);
    }

}