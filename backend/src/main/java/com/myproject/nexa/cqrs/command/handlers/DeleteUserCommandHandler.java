package com.myproject.nexa.cqrs.command.handlers;

import com.myproject.nexa.cqrs.CommandHandler;
import com.myproject.nexa.cqrs.command.DeleteUserCommand;
import com.myproject.nexa.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteUserCommandHandler implements CommandHandler<DeleteUserCommand, Void> {

    private final UserManagementService userManagementService;

    @Override
    public Void handle(DeleteUserCommand command) {
        userManagementService.deleteUser(command.getUserId());
        return null; // Void return
    }
}