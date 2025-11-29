package com.myproject.nexa.security;

import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.services.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Generic rate limiting filter that can be applied to specific endpoints
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    // Define endpoints that should have rate limiting applied
    private final AntPathRequestMatcher[] protectedEndpoints = {
        new AntPathRequestMatcher("/api/v1/auth/**"), // Auth endpoints
        new AntPathRequestMatcher("/api/v1/users/**"), // User endpoints
        new AntPathRequestMatcher("/api/v1/public/contact"), // Public contact form
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if the current endpoint should be rate limited
        boolean shouldCheckRateLimit = false;
        for (AntPathRequestMatcher matcher : protectedEndpoints) {
            if (matcher.matches(request)) {
                shouldCheckRateLimit = true;
                break;
            }
        }

        if (shouldCheckRateLimit) {
            String clientIp = getClientIpAddress(request);
            boolean allowed = rateLimitService.isIpAllowed(clientIp);

            if (!allowed) {
                log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, request.getRequestURI());
                
                // Add rate limit headers
                addRateLimitHeaders(response, clientIp);
                
                // Send rate limit response
                sendRateLimitErrorResponse(response);
                return;
            }

            // Add rate limit headers to the response
            addRateLimitHeaders(response, clientIp);
        }

        filterChain.doFilter(request, response);
    }

    private void addRateLimitHeaders(HttpServletResponse response, String ip) {
        long remaining = rateLimitService.getRemainingRequests("ip:" + ip);
        long resetTime = rateLimitService.getResetTime("ip:" + ip);

        response.setHeader("X-RateLimit-Limit", String.valueOf(100)); // This should match your config
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + resetTime));
    }

    private void sendRateLimitErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse<Object> rateLimitResponse = ApiResponse.error(ErrorCode.SECURITY_002);
        response.getWriter().write(objectMapper.writeValueAsString(rateLimitResponse));
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
}