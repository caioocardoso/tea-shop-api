package com.caiooccardoso.tea_shop_api.controller;

import com.caiooccardoso.tea_shop_api.dto.SignUpDTO;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping
    public SignUpDTO SignUp(SignUpDTO signUpDTO){
        return signUpDTO;
    }
}
