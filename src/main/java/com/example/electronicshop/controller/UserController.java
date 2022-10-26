package com.example.electronicshop.controller;

import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.service.UserService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
 /*   private final UserService userService;


    @GetMapping(path = "/admin/manage/users")
    public ResponseEntity<ResponseObject> findAll (@PageableDefault(size = 5) @ParameterObject Pageable pageable){
        return userService.findAll(pageable);
    }

    @PostMapping(path = "/admin/manage/users")
    public ResponseEntity<?> addUser (@RequestBody RegisterReq req){
        return userService.addUser(req);
    }

    @PutMapping(path = "/admin/manage/users/{userId}")
    public ResponseEntity<?> updateUserAdmin (@Valid @RequestBody UserReq req,
                                              @PathVariable("userId") String userId){
        return userService.updateUser(userId, req);
    }

    @DeleteMapping(path = "/admin/manage/users/{userId}")
    public ResponseEntity<?> deactivatedUser (@PathVariable("userId") String userId){
        return userService.deletedUser(userId);
    }

    @PutMapping(path = "/users/{userId}")
    public ResponseEntity<?> updateUser (@Valid @RequestBody UserReq req,
                                         @PathVariable("userId") String userId,
                                         HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updateUser(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping(path = "/users/password/{userId}")
    public ResponseEntity<?> updatePasswordUser (@Valid @RequestBody ChangePasswordReq req,
                                                 @PathVariable("userId") String userId,
                                                 HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updatePassword(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping(path = "/users/reset/password/{userId}")
    public ResponseEntity<?> updatePasswordReset (@Valid @RequestBody ChangePasswordReq req,
                                                  @PathVariable("userId") String userId,
                                                  HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updatePasswordReset(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PostMapping(path = "/users/avatar/{userId}")
    public ResponseEntity<?> updateUser (@PathVariable("userId") String userId,
                                         HttpServletRequest request,
                                         @RequestParam MultipartFile file){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updateUserAvatar(userId, file);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @GetMapping(path = "/users/{userId}")
    public ResponseEntity<?> findUserById (@PathVariable("userId") String userId,
                                           HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.findUserById(userId);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @GetMapping(path = "/users/order/history")
    public ResponseEntity<?> getUserOrderHistory (HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (!user.getId().isBlank())
            return userService.getUserOrderHistory(user.getId());
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }*/
}
