package com.myproject.nexa.services;

import com.myproject.nexa.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserCacheService {
    void cacheUser(UserResponse user);
    UserResponse getCachedUser(Long userId);
    void evictUser(Long userId);
    void evictAllUsers();
    void cacheAllUsers(Page<UserResponse> users, String cacheKey);
    Page<UserResponse> getCachedAllUsers(String cacheKey);
}