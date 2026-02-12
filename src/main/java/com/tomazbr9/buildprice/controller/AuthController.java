package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.auth.JwtTokenDTO;
import com.tomazbr9.buildprice.dto.auth.LoginDTO;
import com.tomazbr9.buildprice.dto.user.UserRequestDTO;
import com.tomazbr9.buildprice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRequestDTO request){
        service.registerUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDTO> authenticateUser(@RequestBody @Valid LoginDTO request){
        JwtTokenDTO token = service.authenticateUser(request);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
