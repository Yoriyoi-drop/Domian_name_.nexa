package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.impl.UserServiceImpl;
import com.myproject.nexa.utils.AuditLogUtil;
import com.myproject.nexa.utils.ObservabilityUtil;
import com.myproject.nexa.utils.RequestTracingUtil;
import com.myproject.nexa.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObservabilityUtil observabilityUtil;

    @Mock
    private AuditLogUtil auditLogUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createUserRequest = new UserCreateRequest();
        createUserRequest.setUsername("newuser");
        createUserRequest.setEmail("new@example.com");
        createUserRequest.setPassword("Password123!");
        createUserRequest.setFirstName("New");
        createUserRequest.setLastName("User");
        createUserRequest.setEnabled(true);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_password");
        when(observabilityUtil.timeOperation(anyString(), any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, java.util.concurrent.Callable.class).call();
        });
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.createUser(createUserRequest));
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        assertThrows(AppException.class, () -> userService.createUser(createUserRequest));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(AppException.class, () -> userService.createUser(createUserRequest));
    }

    @Test
    void testGetUserById_Found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByIdAndDeletedAtIsNull(1L)).thenReturn(true);

        // Act
        UserResponse response = userService.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppException.class, () -> userService.findById(1L));
    }
}