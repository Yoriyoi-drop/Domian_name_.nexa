package com.myproject.nexa.cqrs.event;

import com.myproject.nexa.cqrs.Event;
import java.time.LocalDateTime;

public class UserDeletedEvent implements Event {
    private final Long userId;
    private final LocalDateTime timestamp;
    
    public UserDeletedEvent(Long userId) {
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}