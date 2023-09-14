package com.example.electronicshop.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RegisterSocial {
    private String name;
    @Size(max = 50)
    @Email
    private String email;
    private String avatar;
    //    private String address;
    private String role;
}