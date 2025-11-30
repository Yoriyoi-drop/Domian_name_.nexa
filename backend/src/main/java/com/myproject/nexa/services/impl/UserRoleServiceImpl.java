package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.response.RoleResponse;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.exceptions.ResourceNotFoundException;
import com.myproject.nexa.mapper.RoleMapper;
import com.myproject.nexa.mapper.UserMapper;
import com.myproject.nexa.repositories.RoleRepository;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.debug("Getting all roles");
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toResponseList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    public UserResponse assignRoleToUser(Long userId, String roleName) {
        log.debug("Assigning role '{}' to user with ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        // Add role to user if not already assigned
        if (user.getRoles() == null) {
            user.setRoles(List.of(role));
        } else if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        } else {
            throw new AppException(ErrorCode.BUSINESS_001, "User already has the role: " + roleName);
        }

        User updatedUser = userRepository.save(user);
        log.info("Role '{}' assigned to user ID: {}", roleName, userId);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse removeRoleFromUser(Long userId, String roleName) {
        log.debug("Removing role '{}' from user with ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        // Remove role if user has it
        if (user.getRoles() != null && user.getRoles().remove(role)) {
            User updatedUser = userRepository.save(user);
            log.info("Role '{}' removed from user ID: {}", roleName, userId);
            return userMapper.toResponse(updatedUser);
        } else {
            throw new AppException(ErrorCode.BUSINESS_001, "User does not have the role: " + roleName);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getUserRoles(Long userId) {
        log.debug("Getting roles for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRoles() == null) {
            return List.of();
        }

        return roleMapper.toResponseList(user.getRoles());
    }
}