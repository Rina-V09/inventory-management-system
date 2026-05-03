package com.retailnet.inventory.service;

import com.retailnet.inventory.dto.UserRegistrationRequest;

public interface KeycloakUserService {

    /**
     * Creates a new user in Keycloak based on the provided registration request.
     * 
     * @param request the user registration details
     * @return true if successful, false otherwise
     */
    boolean createUser(UserRegistrationRequest request);

}
