package com.myproject.nexa.cqrs.query.handlers;

import com.myproject.nexa.cqrs.QueryHandler;
import com.myproject.nexa.cqrs.query.GetUserQuery;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, UserResponse> {

    private final UserManagementService userManagementService;

    @Override
    public UserResponse handle(GetUserQuery query) {
        return userManagementService.getUserById(query.getUserId());
    }
}