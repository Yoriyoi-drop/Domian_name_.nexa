package com.myproject.nexa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTenantRequest {
    
    @NotBlank(message = "Tenant name is required")
    @Size(min = 3, max = 50, message = "Tenant name must be between 3 and 50 characters")
    private String name;
    
    @NotBlank(message = "Subdomain is required")
    @Size(min = 3, max = 50, message = "Subdomain must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]$", message = "Subdomain must contain alphanumeric characters and hyphens only, cannot start or end with hyphen")
    private String subdomain;
    
    @NotBlank(message = "Display name is required")
    @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters")
    private String displayName;
    
    private String description;
}