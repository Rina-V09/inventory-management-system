package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.UserController;
import com.retailnet.inventory.dto.UserRegistrationRequest;
import com.retailnet.inventory.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final KeycloakUserService keycloakUserService;

    @Override
    public ResponseEntity<String> registerUser(UserRegistrationRequest request) {
        log.info("Received registration request for username: {}", request.getUsername());
        
        boolean success = keycloakUserService.createUser(request);
        
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register user");
        }
    }
}
