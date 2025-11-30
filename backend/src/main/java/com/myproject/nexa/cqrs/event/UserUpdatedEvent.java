package com.myproject.nexa.cqrs.event;

import com.myproject.nexa.cqrs.Event;
import java.time.LocalDateTime;

public class UserUpdatedEvent implements Event {
    private final Long userId;
    private final String username;
    private final String email;
    private final LocalDateTime timestamp;
    
    public UserUpdatedEvent(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.timestamp = LocalDateTime.now();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}