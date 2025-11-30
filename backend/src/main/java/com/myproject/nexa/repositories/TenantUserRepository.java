package com.myproject.nexa.repositories;

import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.TenantUser;
import com.myproject.nexa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    
    List<TenantUser> findByTenant(Tenant tenant);
    
    List<TenantUser> findByUser(User user);
    
    Optional<TenantUser> findByTenantAndUser(Tenant tenant, User user);
    
    @Query("SELECT tu FROM TenantUser tu JOIN tu.tenant t JOIN tu.user u WHERE t.subdomain = :subdomain AND u.username = :username")
    Optional<TenantUser> findBySubdomainAndUsername(@Param("subdomain") String subdomain, @Param("username") String username);
    
    long countByTenant(Tenant tenant);
    
    boolean existsByTenantAndUser(Tenant tenant, User user);
}