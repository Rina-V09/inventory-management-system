package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.dto.UserRegistrationRequest;
import com.retailnet.inventory.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Service implementation for interacting with Keycloak to manage users.
 * Handles admin authentication and user creation in Keycloak realm.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {

    // RestTemplate used to make HTTP calls to Keycloak APIs
    private final RestTemplate restTemplate = new RestTemplate();

    // Base URL of Keycloak server
    @Value("${keycloak.server-url:http://localhost:8080}")
    private String serverUrl;

    // Target realm where users will be created
    @Value("${keycloak.realm:retailnet}")
    private String realm;

    // Admin realm used for authentication (usually "master")
    @Value("${keycloak.admin.realm:master}")
    private String adminRealm;

    // Admin client ID for authentication
    @Value("${keycloak.admin.client-id:retailnet-ui}")
    private String clientId;

    // Admin username for Keycloak login
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    // Admin password for Keycloak login
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    /**
     * Authenticates with Keycloak using admin credentials
     * and retrieves an access token.
     *
     * @return access token string if successful, otherwise null
     */
    private String getAdminToken() {
        // Token endpoint URL
        String tokenUrl = serverUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";

        // Set headers for form-urlencoded request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Request body parameters required by Keycloak
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        // Wrap body and headers into HttpEntity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // Call Keycloak token API
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            // Extract access token if response is successful
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.error("Failed to authenticate as Keycloak admin.", e);
        }
        return null;
    }

    /**
     * Creates a new user in Keycloak realm.
     *
     * @param request User registration details (username, email, password)
     * @return true if user created successfully, false otherwise
     */
    @Override
    public boolean createUser(UserRegistrationRequest request) {
        // Get admin access token
        String token = getAdminToken();
        if (token == null) {
            return false;
        }

        // Keycloak user creation endpoint
        String createUserUrl = serverUrl + "/admin/realms/" + realm + "/users";

        // Set headers with Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Build user payload
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("username", request.getUsername());
        userBody.put("email", request.getEmail());
        userBody.put("enabled", true);

        // Build credentials (password configuration)
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", request.getPassword());
        credentials.put("temporary", false);

        // Attach credentials to user
        userBody.put("credentials", Collections.singletonList(credentials));

        // Wrap request body and headers
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userBody, headers);

        try {
            // Call Keycloak API to create user
            ResponseEntity<String> response = restTemplate.postForEntity(createUserUrl, entity, String.class);

            // Handle response status
            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("User created successfully in Keycloak: {}", request.getUsername());
                return true;

            } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
                log.error("User already exists: {}", request.getUsername());
                return false;

            } else {
                log.error("Failed to create user in Keycloak, status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception while creating user in Keycloak", e);
            return false;
        }
    }
}