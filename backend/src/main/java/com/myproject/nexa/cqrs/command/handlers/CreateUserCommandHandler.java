package com.myproject.nexa.cqrs.command.handlers;

import com.myproject.nexa.cqrs.CommandHandler;
import com.myproject.nexa.cqrs.command.CreateUserCommand;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, UserResponse> {

    private final UserManagementService userManagementService;

    @Override
    public UserResponse handle(CreateUserCommand command) {
        return userManagementService.createUser(command.getRequest());
    }
}