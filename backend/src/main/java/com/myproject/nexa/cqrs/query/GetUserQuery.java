package com.myproject.nexa.cqrs.query;

import com.myproject.nexa.cqrs.Query;
import org.springframework.data.domain.Pageable;

public class GetUserQuery implements Query {
    private final Long userId;
    
    public GetUserQuery(Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
}