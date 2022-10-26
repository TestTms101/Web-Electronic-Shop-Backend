package com.example.electronicshop.controller;


import com.example.electronicshop.communication.request.LoginRequest;
import com.example.electronicshop.communication.request.Register;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login( @RequestBody LoginRequest loginReq) {
        return authService.login(loginReq);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register( @RequestBody Register registerReq) {
        return authService.register(registerReq);
    }


}
