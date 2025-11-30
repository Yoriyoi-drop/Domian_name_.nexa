package com.myproject.nexa.repositories;

import com.myproject.nexa.entities.TenantAwareBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface TenantAwareRepository<T extends TenantAwareBaseEntity, ID extends Serializable> 
    extends JpaRepository<T, ID> {
    
    List<T> findByTenantId(Long tenantId);
}