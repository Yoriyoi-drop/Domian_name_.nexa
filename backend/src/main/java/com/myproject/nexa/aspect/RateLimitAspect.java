package com.myproject.nexa.aspect;

import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.services.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect for applying rate limiting to specific methods/endpoints
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Pointcut("@annotation(rateLimited)")
    public void rateLimitedMethod(com.myproject.nexa.annotation.RateLimited rateLimited) {}

    @Pointcut("execution(* com.myproject.nexa.controllers.*.*(..))")
    public void controllerMethod() {}

    @Before("controllerMethod() && rateLimited(rateLimited)")
    public void checkRateLimit(com.myproject.nexa.annotation.RateLimited rateLimited) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return; // Not in a web request context
        }

        HttpServletRequest request = attributes.getRequest();
        
        // Determine rate limit key based on annotation configuration
        String key = generateRateLimitKey(request, rateLimited);
        
        // Check if request is allowed
        boolean allowed = rateLimitService.isAllowed(key, rateLimited.limit(), rateLimited.window());
        
        if (!allowed) {
            log.warn("Rate limit exceeded for key: {} and endpoint: {}", key, request.getRequestURI());
            throw new AppException(ErrorCode.SECURITY_002); // Too many requests
        }
    }

    private String generateRateLimitKey(HttpServletRequest request, com.myproject.nexa.annotation.RateLimited rateLimited) {
        String key;
        
        switch (rateLimited.by()) {
            case IP:
                key = "ip:" + getClientIpAddress(request);
                break;
            case USER:
                // Attempt to get user ID from security context
                key = "user:" + getCurrentUserId();
                if ("user:null".equals(key) || "user:anonymousUser".equals(key)) {
                    // Fall back to IP if no user is authenticated
                    key = "ip:" + getClientIpAddress(request);
                }
                break;
            case ENDPOINT:
                key = "endpoint:" + request.getRequestURI();
                break;
            case CUSTOM:
                // Use the custom key if provided
                String customKey = rateLimited.customKey();
                if (customKey != null && !customKey.isEmpty()) {
                    key = customKey;
                } else {
                    key = "ip:" + getClientIpAddress(request);
                }
                break;
            default:
                key = "ip:" + getClientIpAddress(request);
                break;
        }
        
        return key;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // Get the first IP if multiple are present
            int idx = xForwardedFor.indexOf(",");
            if (idx != -1) {
                return xForwardedFor.substring(0, idx);
            } else {
                return xForwardedFor;
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String remoteAddr = request.getRemoteAddr();
        // Handle IPv6 localhost
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            return "127.0.0.1";
        }

        return remoteAddr;
    }

    private String getCurrentUserId() {
        // This would typically get the user ID from Spring Security context
        // Implementation depends on your security setup
        // For now, returning null - would be implemented based on your authentication system
        return null;
    }
}