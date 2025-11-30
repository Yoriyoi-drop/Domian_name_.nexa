package com.myproject.nexa.cqrs;

import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.cqrs.query.GetAllUsersQuery;
import com.myproject.nexa.cqrs.query.GetUserQuery;
import com.myproject.nexa.cqrs.query.handlers.GetAllUsersQueryHandler;
import com.myproject.nexa.cqrs.query.handlers.GetUserQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryBus {

    private final GetUserQueryHandler getUserQueryHandler;
    private final GetAllUsersQueryHandler getAllUsersQueryHandler;

    public UserResponse handle(GetUserQuery query) {
        return getUserQueryHandler.handle(query);
    }

    public Page<UserResponse> handle(GetAllUsersQuery query) {
        return getAllUsersQueryHandler.handle(query);
    }
}