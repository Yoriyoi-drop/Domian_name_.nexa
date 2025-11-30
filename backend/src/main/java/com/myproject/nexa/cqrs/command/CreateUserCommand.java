package com.myproject.nexa.cqrs.command;

import com.myproject.nexa.cqrs.Command;
import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;

public class CreateUserCommand implements Command {
    private final UserCreateRequest request;
    
    public CreateUserCommand(UserCreateRequest request) {
        this.request = request;
    }
    
    public UserCreateRequest getRequest() {
        return request;
    }
}