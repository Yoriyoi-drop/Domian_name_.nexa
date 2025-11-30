package com.myproject.nexa.cqrs.query.handlers;

import com.myproject.nexa.cqrs.QueryHandler;
import com.myproject.nexa.cqrs.query.GetAllUsersQuery;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllUsersQueryHandler implements QueryHandler<GetAllUsersQuery, Page<UserResponse>> {

    private final UserManagementService userManagementService;

    @Override
    public Page<UserResponse> handle(GetAllUsersQuery query) {
        return userManagementService.getAllUsers(query.getPageable());
    }
}