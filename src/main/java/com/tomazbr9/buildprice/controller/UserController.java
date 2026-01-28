package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDTO> userData(@AuthenticationPrincipal String username){
        UserResponseDTO user = service.userData(username);
        return ResponseEntity.ok().body(user);

    }

    @GetMapping("/admin/me")
    public ResponseEntity<UserResponseDTO> adminData(@AuthenticationPrincipal String username){
        UserResponseDTO user = service.userData(username);
        return ResponseEntity.ok().body(user);

    }
}
