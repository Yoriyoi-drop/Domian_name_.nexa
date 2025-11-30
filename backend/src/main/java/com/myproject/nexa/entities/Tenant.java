package com.myproject.nexa.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tenants", indexes = {
        @Index(name = "idx_tenant_subdomain", columnList = "subdomain"),
        @Index(name = "idx_tenant_name", columnList = "name"),
        @Index(name = "idx_tenant_status", columnList = "status"),
        @Index(name = "idx_tenant_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@FieldNameConstants
public class Tenant extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "subdomain", nullable = false, unique = true)
    private String subdomain;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    @Column(name = "configuration", columnDefinition = "jsonb")
    private String configuration; // JSON configuration for tenant-specific settings

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_storage_mb")
    private Integer maxStorageMB;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    public enum TenantStatus {
        ACTIVE, INACTIVE, SUSPENDED, TRIAL_EXPIRED, DELETED
    }
}