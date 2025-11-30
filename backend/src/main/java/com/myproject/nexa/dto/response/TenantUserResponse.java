package com.myproject.nexa.dto.response;

import com.myproject.nexa.entities.TenantUser;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TenantUserResponse {
    
    private Long id;
    private Long tenantId;
    private String tenantName;
    private Long userId;
    private String username;
    private TenantUser.TenantUserRole role;
    private boolean isActive;
    private LocalDateTime assignedAt;
    private LocalDateTime removedAt;
    
    public static TenantUserResponse fromEntity(TenantUser tenantUser) {
        return TenantUserResponse.builder()
                .id(tenantUser.getId())
                .tenantId(tenantUser.getTenant().getId())
                .tenantName(tenantUser.getTenant().getName())
                .userId(tenantUser.getUser().getId())
                .username(tenantUser.getUser().getUsername())
                .role(tenantUser.getRole())
                .isActive(tenantUser.isActive())
                .assignedAt(tenantUser.getAssignedAt())
                .removedAt(tenantUser.getRemovedAt())
                .build();
    }
}