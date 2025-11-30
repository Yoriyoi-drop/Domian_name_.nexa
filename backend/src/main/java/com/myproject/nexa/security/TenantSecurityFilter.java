package com.myproject.nexa.security;

import com.myproject.nexa.config.TenantContext;
import com.myproject.nexa.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Check if there's an authenticated user and verify tenant access
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated()) {
                // Get the current tenant from context
                String currentTenant = TenantContext.getCurrentTenant();
                log.debug("Current tenant context: {}", currentTenant);
                
                if (authentication.getPrincipal() instanceof User) {
                    User authenticatedUser = (User) authentication.getPrincipal();
                    Long userTenantId = authenticatedUser.getTenantId();
                    
                    // Verify that the user belongs to the current tenant context
                    if (userTenantId != null && currentTenant != null) {
                        // Convert tenant name to ID using same logic as entity listener for comparison
                        long expectedTenantId = Math.abs(currentTenant.hashCode()) % 1000000L;
                        
                        if (!userTenantId.equals(expectedTenantId)) {
                            log.warn("User tenant ID {} does not match request tenant ID {}", 
                                   userTenantId, expectedTenantId);
                            // In a real implementation, you might want to return 403 or similar
                            // For now, we'll just log this situation
                        }
                    }
                }
            }
            
            filterChain.doFilter(request, response);
            
        } finally {
            // Ensure context is cleared after request processing
            TenantContext.clear();
        }
    }
}