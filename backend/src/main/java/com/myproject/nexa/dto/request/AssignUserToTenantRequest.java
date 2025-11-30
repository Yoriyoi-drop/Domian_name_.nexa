package com.myproject.nexa.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignUserToTenantRequest {
    
    @NotNull(message = "Tenant ID is required")
    private Long tenantId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String role; // OWNER, ADMIN, MEMBER, GUEST
}