package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.CommentReq;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.security.jwt.JwtUtils;
import com.example.electronicshop.service.CommentService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final JwtUtils jwtUtils;

    @GetMapping(path = "/comment/{productId}")
    public ResponseEntity<?> findByProductId(@PathVariable("productId") String productId,
                                             @SortDefault(sort = "createdDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
        return commentService.findByProductId(productId, pageable);
    }

    @GetMapping(path = "/admin/manage/comment/findall")
    public ResponseEntity<?> findAllComment(@RequestParam("sortBy") String sortBy,
                                            @RequestParam("state") String state,
                                            @ParameterObject Pageable pageable) {
        return commentService.findAllComment(sortBy, state, pageable);
    }

    @GetMapping(path = "/admin/comment/{productId}")
    public ResponseEntity<?> search(@PathVariable("productId") String productId,
                                    @RequestParam("sortBy") String sortBy,
                                    @RequestParam("state") String state,
                                    @ParameterObject Pageable pageable) {
        return commentService.findByProductIdAndCreateDateAndState(productId, sortBy, state, pageable);
    }

    @GetMapping(path = "/admin/comment/count")
    public ResponseEntity<?> getCountByState() {
        return commentService.getAllCountComments();
    }

    @GetMapping(path = "/comment/datedesc/{productId}")
    public ResponseEntity<?> findByProductIdOrderByDateDESC(@PathVariable("productId") String productId,
                                                            @SortDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                                            @ParameterObject Pageable pageable) {
        return commentService.findByProductId(productId, pageable);
    }

    @GetMapping(path = "/comment/dateasc/{productId}")
    public ResponseEntity<?> findByProductIdOrderByDateASC(@PathVariable("productId") String productId,
                                                           @SortDefault(sort = "createdDate", direction = Sort.Direction.ASC)
                                                           @ParameterObject Pageable pageable) {
        return commentService.findByProductId(productId, pageable);
    }

    @PostMapping(path = "/comment")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentReq req,
                                        HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (!user.getId().isBlank())
            return commentService.addComment(user.getId(), req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping(path = "/comment/edit/{CommentId}")
    public ResponseEntity<?> editCommentbyUser(@PathVariable("CommentId") String CommentId
            , @RequestBody CommentReq req, HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (!user.getId().isBlank())
            return commentService.editCommentByUser(CommentId, user.getId(), req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping("/admin/manage/comment/setenable/{CommentId}")
    public ResponseEntity<?> setEnableComment(@PathVariable("CommentId") String CommentId) {
        return commentService.setEnableComment(CommentId);
    }

    @DeleteMapping("/admin/manage/comment/block/{CommentId}")
    public ResponseEntity<?> blockComment(@PathVariable("CommentId") String CommentId) {
        return commentService.blockComment(CommentId);
    }

    @DeleteMapping("/admin/manage/comment/delete/{CommentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("CommentId") String CommentId, HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (!user.getId().isBlank())
            return commentService.deleteCommemt(CommentId);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @DeleteMapping("/comment/deletebyuser/{CommentId}")
    public ResponseEntity<?> deleteCommentByUser(@PathVariable("CommentId") String CommentId, HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (!user.getId().isBlank())
            return commentService.deleteCommemtbyUser(CommentId, user.getId());
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }
}
