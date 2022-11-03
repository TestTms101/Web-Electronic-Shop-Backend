package com.example.electronicshop.map;

import com.example.electronicshop.models.enity.Comment;
import com.example.electronicshop.communication.response.CommentRes;
import org.springframework.stereotype.Service;

@Service
public class CommentMapper {
    public CommentRes toReviewRes(Comment req) {
        return new CommentRes(req.getId(), req.getContent(), req.getRate(),
                req.isEnable(), req.getUser().getName(), req.getCreatedDate());
    }
}
