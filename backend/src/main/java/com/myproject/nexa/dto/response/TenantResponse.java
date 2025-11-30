package com.myproject.nexa.dto.response;

import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TenantResponse {
    
    private Long id;
    private String name;
    private String subdomain;
    private String displayName;
    private String description;
    private Tenant.TenantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TenantResponse fromEntity(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .subdomain(tenant.getSubdomain())
                .displayName(tenant.getDisplayName())
                .description(tenant.getDescription())
                .status(tenant.getStatus())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}