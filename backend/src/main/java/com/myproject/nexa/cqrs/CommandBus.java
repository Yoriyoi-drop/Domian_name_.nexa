package com.myproject.nexa.cqrs;

import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.cqrs.command.CreateUserCommand;
import com.myproject.nexa.cqrs.command.DeleteUserCommand;
import com.myproject.nexa.cqrs.command.UpdateUserCommand;
import com.myproject.nexa.cqrs.command.handlers.CreateUserCommandHandler;
import com.myproject.nexa.cqrs.command.handlers.DeleteUserCommandHandler;
import com.myproject.nexa.cqrs.command.handlers.UpdateUserCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandBus {

    private final CreateUserCommandHandler createUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final DeleteUserCommandHandler deleteUserCommandHandler;

    public UserResponse handle(CreateUserCommand command) {
        return createUserCommandHandler.handle(command);
    }

    public UserResponse handle(UpdateUserCommand command) {
        return updateUserCommandHandler.handle(command);
    }

    public void handle(DeleteUserCommand command) {
        deleteUserCommandHandler.handle(command);
    }
}