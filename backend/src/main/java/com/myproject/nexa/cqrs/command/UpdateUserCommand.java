package com.myproject.nexa.cqrs.command;

import com.myproject.nexa.cqrs.Command;
import com.myproject.nexa.dto.request.UserUpdateRequest;

public class UpdateUserCommand implements Command {
    private final Long userId;
    private final UserUpdateRequest request;
    
    public UpdateUserCommand(Long userId, UserUpdateRequest request) {
        this.userId = userId;
        this.request = request;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public UserUpdateRequest getRequest() {
        return request;
    }
}