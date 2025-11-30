package com.myproject.nexa.entities;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import com.myproject.nexa.config.TenantContext;

@Slf4j
public class TenantAwareEntityListener {

    @PrePersist
    public void setTenantOnCreate(Object entity) {
        if (entity instanceof TenantAwareBaseEntity) {
            String currentTenant = TenantContext.getCurrentTenant();
            if (currentTenant != null && !currentTenant.isEmpty()) {
                // For simplicity, we'll use hash of tenant name as ID
                // In a real application, you might want to store actual tenant IDs
                long tenantId = Math.abs(currentTenant.hashCode()) % 1000000L; // Keep it within reasonable range
                ((TenantAwareBaseEntity) entity).setTenantId(tenantId);
                log.debug("Setting tenant ID {} for entity {} on persist", tenantId, entity.getClass().getSimpleName());
            } else {
                log.warn("No tenant context available when persisting entity {}", entity.getClass().getSimpleName());
            }
        }
    }

    @PreUpdate
    public void setTenantOnUpdate(Object entity) {
        if (entity instanceof TenantAwareBaseEntity) {
            String currentTenant = TenantContext.getCurrentTenant();
            if (currentTenant != null && !currentTenant.isEmpty()) {
                long tenantId = Math.abs(currentTenant.hashCode()) % 1000000L;
                ((TenantAwareBaseEntity) entity).setTenantId(tenantId);
                log.debug("Setting tenant ID {} for entity {} on update", tenantId, entity.getClass().getSimpleName());
            } else {
                log.warn("No tenant context available when updating entity {}", entity.getClass().getSimpleName());
            }
        }
    }
}