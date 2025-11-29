package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Request tracing utility with correlation ID support
 */
@Component
@Slf4j
public class RequestTracingUtil implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final ThreadLocal<String> correlationIdHolder = new ThreadLocal<>();

    /**
     * Generate or retrieve correlation ID for the request
     */
    public static String getOrCreateCorrelationId() {
        String correlationId = correlationIdHolder.get();
        if (correlationId == null) {
            correlationId = generateCorrelationId();
            correlationIdHolder.set(correlationId);
        }
        return correlationId;
    }

    /**
     * Get current correlation ID (returns null if not set)
     */
    public static String getCurrentCorrelationId() {
        return correlationIdHolder.get();
    }

    /**
     * Set correlation ID for current thread
     */
    public static void setCorrelationId(String correlationId) {
        correlationIdHolder.set(correlationId);
    }

    /**
     * Clear correlation ID for current thread
     */
    public static void clearCorrelationId() {
        correlationIdHolder.remove();
    }

    private static String generateCorrelationId() {
        return "corr-" + UUID.randomUUID().toString();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get or create correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId();
        }
        
        // Store correlation ID in thread local
        correlationIdHolder.set(correlationId);
        
        // Add to response headers
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Log request start
        log.info("REQUEST_START - CorrelationID: {} - Method: {} - URI: {} - RemoteAddr: {}",
                correlationId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String correlationId = correlationIdHolder.get();
        
        // Log request completion
        log.info("REQUEST_END - CorrelationID: {} - Status: {} - URI: {}",
                correlationId, response.getStatus(), request.getRequestURI());
        
        // Clear correlation ID from thread local
        correlationIdHolder.remove();
    }

    /**
     * Log a custom trace event with correlation ID
     */
    public static void logTraceEvent(String level, String message, Object... args) {
        String correlationId = getCurrentCorrelationId();
        String fullMessage = correlationId != null ? 
            String.format("CORR_ID: %s - %s", correlationId, message) : message;
        
        switch (level.toUpperCase()) {
            case "ERROR":
                log.error(fullMessage, args);
                break;
            case "WARN":
                log.warn(fullMessage, args);
                break;
            case "INFO":
                log.info(fullMessage, args);
                break;
            case "DEBUG":
                log.debug(fullMessage, args);
                break;
            default:
                log.info(fullMessage, args);
                break;
        }
    }

    /**
     * Add correlation ID to MDC for logging frameworks
     */
    public static void addToMDC() {
        String correlationId = getCurrentCorrelationId();
        if (correlationId != null) {
            org.slf4j.MDC.put("correlationId", correlationId);
        }
    }

    /**
     * Remove correlation ID from MDC
     */
    public static void removeFromMDC() {
        org.slf4j.MDC.remove("correlationId");
    }
}