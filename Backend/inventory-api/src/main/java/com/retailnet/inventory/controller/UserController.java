package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public interface UserController {

    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request);
}
