package com.myproject.nexa.unit;

import com.myproject.nexa.config.properties.AppProperties;
import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.RoleRepository;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.impl.UserServiceImpl;
import com.myproject.nexa.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceSecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppProperties appProperties;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private com.myproject.nexa.utils.ObservabilityUtil observabilityUtil;

    @Mock
    private com.myproject.nexa.utils.AuditLogUtil auditLogUtil;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder, observabilityUtil, auditLogUtil, securityUtil);
    }

    @Test
    void testCreateUserWithXssInUsername() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("<script>alert('xss')</script>");
        request.setEmail("test@example.com");
        request.setPassword("TestPass123!@#");

        // Configure mocks
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        // Expect an exception due to input validation
        assertThrows(AppException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUserWithValidData() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("validuser");
        request.setEmail("valid@example.com");
        request.setPassword("TestPass123!@#");
        request.setEnabled(true);

        // Configure mocks
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        // This should succeed
        assertDoesNotThrow(() -> {
            userService.createUser(request);
        });
    }

    @Test
    void testValidateUserCreationWithWeakPassword() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("weak"); // Too weak

        assertThrows(AppException.class, () -> {
            userService.validateUserCreation(request);
        });
    }
}