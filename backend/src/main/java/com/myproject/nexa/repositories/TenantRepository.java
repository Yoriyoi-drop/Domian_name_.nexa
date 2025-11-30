package com.myproject.nexa.repositories;

import com.myproject.nexa.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findBySubdomain(String subdomain);
    
    Optional<Tenant> findByName(String name);
    
    @Query("SELECT t FROM Tenant t WHERE t.subdomain = :subdomain AND t.status = 'ACTIVE'")
    Optional<Tenant> findActiveBySubdomain(@Param("subdomain") String subdomain);

    boolean existsBySubdomain(String subdomain);
    
    boolean existsByName(String name);
}