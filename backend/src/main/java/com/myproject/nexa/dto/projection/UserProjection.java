package com.myproject.nexa.dto.projection;

import java.time.LocalDateTime;

public interface UserProjection {
    Long getId();
    String getUsername();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getPhone();
    String getAddress();
    Boolean getEnabled();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}