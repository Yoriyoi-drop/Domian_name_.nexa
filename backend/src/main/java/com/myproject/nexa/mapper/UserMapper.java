package com.myproject.nexa.mapper;

import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared mapper for User entity to UserResponse conversion
 * Eliminates code duplication across multiple service implementations
 */
@Component
public class UserMapper {

    /**
     * Maps a User entity to UserResponse DTO
     * @param user The User entity to map
     * @return UserResponse DTO or null if user is null
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .build();
    }

    /**
     * Backward compatibility method - same as toUserResponse
     * @param user The User entity to map
     * @return UserResponse DTO or null if user is null
     */
    public UserResponse mapToUserResponse(User user) {
        return toUserResponse(user);
    }

    /**
     * Another backward compatibility method - same as toUserResponse
     * Used by UserRoleService and other services
     * @param user The User entity to map
     * @return UserResponse DTO or null if user is null
     */
    public UserResponse toResponse(User user) {
        return toUserResponse(user);
    }

    /**
     * Maps a list of User entities to a list of UserResponse DTOs
     * @param users The list of User entities to map
     * @return List of UserResponse DTOs
     */
    public List<UserResponse> toUserResponseList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }
}