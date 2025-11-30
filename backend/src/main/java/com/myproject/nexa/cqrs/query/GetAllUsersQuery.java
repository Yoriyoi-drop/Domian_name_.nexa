package com.myproject.nexa.cqrs.query;

import com.myproject.nexa.cqrs.Query;
import org.springframework.data.domain.Pageable;

public class GetAllUsersQuery implements Query {
    private final Pageable pageable;
    
    public GetAllUsersQuery(Pageable pageable) {
        this.pageable = pageable;
    }
    
    public Pageable getPageable() {
        return pageable;
    }
}