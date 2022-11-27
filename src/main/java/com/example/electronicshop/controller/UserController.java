package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.ChangePassword;
import com.example.electronicshop.communication.request.Register;
import com.example.electronicshop.communication.request.ResetPassRequest;
import com.example.electronicshop.communication.request.UserRequest;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.security.jwt.JwtUtils;
import com.example.electronicshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping(path = "/admin/manage/users")
    public ResponseEntity<ResponseObject> findAll (@PageableDefault(size = 5) @ParameterObject Pageable pageable){
        return userService.findAll(pageable);
    }

    @PostMapping(path = "/admin/manage/users")
    public ResponseEntity<?> addUser (@RequestBody Register req){
        return userService.addUser(req);
    }



    @DeleteMapping(path = "/admin/manage/users/{userId}")
    public ResponseEntity<?> deactivatedUser (@PathVariable("userId") String userId){
        return userService.deletedUser(userId);
    }
    @PutMapping(path = "/users/password/{userId}")
    public ResponseEntity<?> updatePasswordUser ( @RequestBody ChangePassword req,
                                                  @PathVariable("userId") String userId,
                                                  HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updatePassword(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping(path = "/users/resetpassword/{userId}")
    public ResponseEntity<?> updateResetPasswordUser ( @RequestBody ResetPassRequest resetPassRequest,
                                                  @PathVariable("userId") String userId,
                                                  HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updatePassUser(userId,resetPassRequest);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

    @PutMapping(path = "/users/{userId}")
    public ResponseEntity<?> updateUser ( @RequestBody UserRequest req,
                                          @PathVariable("userId") String userId,
                                          HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updateUser(userId, req);
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

    @PostMapping(path = "/users/avatar/{userId}")
    public ResponseEntity<?> updateUserAvatar (@PathVariable("userId") String userId,
                                         HttpServletRequest request,
                                               @RequestParam (value = "avatar") MultipartFile file){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId) || !user.getId().isBlank())
            return userService.updateUserAvatar(userId, file);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "You don't have permission! Token is invalid");
    }

}
