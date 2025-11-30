package com.myproject.nexa.cqrs.command;

import com.myproject.nexa.cqrs.Command;

public class DeleteUserCommand implements Command {
    private final Long userId;
    
    public DeleteUserCommand(Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
}