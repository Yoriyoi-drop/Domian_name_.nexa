package com.myproject.nexa.cqrs.command.handlers;

import com.myproject.nexa.cqrs.CommandHandler;
import com.myproject.nexa.cqrs.command.UpdateUserCommand;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserCommandHandler implements CommandHandler<UpdateUserCommand, UserResponse> {

    private final UserManagementService userManagementService;

    @Override
    public UserResponse handle(UpdateUserCommand command) {
        return userManagementService.updateUser(command.getUserId(), command.getRequest());
    }
}