package com.myproject.nexa.integration;

import com.myproject.nexa.config.TestContainerConfig;
import com.myproject.nexa.dto.request.RegisterRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.AuthResponse;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRegistrationSecurityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String getRootUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    void testUserRegistrationWithValidData() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser123");
        request.setEmail("test@example.com");
        request.setPassword("TestPass123!@#");
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
                getRootUrl() + "/register",
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify user was created in database
        assertTrue(userRepository.findByUsername("testuser123").isPresent());
    }

    @Test
    void testUserRegistrationWithWeakPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("weakpassuser");
        request.setEmail("weak@example.com");
        request.setPassword("weak"); // This should fail validation

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
                getRootUrl() + "/register",
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        // Should return bad request due to password validation
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // Verify user was not created in database
        assertFalse(userRepository.findByUsername("weakpassuser").isPresent());
    }

    @Test
    void testUserRegistrationWithXssAttempt() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("<script>alert('xss')</script>");
        request.setEmail("xss@example.com");
        request.setPassword("TestPass123!@#");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
                getRootUrl() + "/register",
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        // Should return bad request due to validation
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // Verify user was not created in database
        assertFalse(userRepository.findByUsername("<script>alert('xss')</script>").isPresent());
    }
}