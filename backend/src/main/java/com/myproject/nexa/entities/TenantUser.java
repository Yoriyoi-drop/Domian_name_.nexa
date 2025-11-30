package com.myproject.nexa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private TenantUserRole role;
    
    private boolean isActive;
    private LocalDateTime assignedAt;
    private LocalDateTime removedAt;
    
    // Enum untuk peran di tenant
    public enum TenantUserRole {
        OWNER, ADMIN, MEMBER, GUEST
    }
}