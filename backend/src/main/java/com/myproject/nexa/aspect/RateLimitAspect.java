package com.myproject.nexa.aspect;

import com.myproject.nexa.annotation.RateLimit;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.services.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String clientIp = getClientIpAddress(request);
        String key = rateLimit.key().isEmpty() ? generateDefaultKey(request) : rateLimit.key();
        String rateLimitKey = key + ":" + clientIp; // Combine key with client IP

        boolean allowed = rateLimitService.isAllowed(
            rateLimitKey,
            rateLimit.limit(),
            rateLimit.window(),
            rateLimit.unit()
        );

        if (!allowed) {
            log.warn("Rate limit exceeded for IP: {}, endpoint: {}", clientIp, request.getRequestURI());
            throw new AppException(ErrorCode.SECURITY_002, rateLimit.message());
        }

        return joinPoint.proceed();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private String generateDefaultKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return method + ":" + uri;
    }
}