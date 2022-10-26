package com.example.electronicshop.service;

import com.example.electronicshop.communication.request.LoginRequest;
import com.example.electronicshop.communication.request.Register;
import com.example.electronicshop.communication.response.LoginResponese;
import com.example.electronicshop.map.UserMap;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.enity.User;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.repository.UserRepository;
import com.example.electronicshop.security.jwt.JwtUtils;
import com.example.electronicshop.security.userdetail.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class AuthService {
    private AuthenticationManager authenticationManager;
    private final UserMap userMapper;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
   public ResponseEntity<ResponseObject> login(LoginRequest req) {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            String accesstoken = jwtUtils.generateTokenFromUserId(user.getUser());
            LoginResponese res = userMapper.toLoginRes(user.getUser());
            res.setAccessToken(accesstoken);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("true", "Log in successfully ", res)
            );


    }


    public ResponseEntity<ResponseObject> register(Register req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new AppException(HttpStatus.CONFLICT.value(), "Email  exists");
        req.setPassword(passwordEncoder.encode(req.getPassword()));
        User user = userMapper.toUser(req);
        user.setToken(null);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("true", "Register ", user)
        );
    }
}
