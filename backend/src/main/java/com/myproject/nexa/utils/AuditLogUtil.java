package com.myproject.nexa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Audit logging utility for tracking important operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogUtil {

    private final ObjectMapper objectMapper;

    /**
     * Log user action in audit trail
     */
    public void logUserAction(String userId, String action, String resource, Map<String, Object> details) {
        try {
            AuditLogEntry auditLog = AuditLogEntry.builder()
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .userId(userId)
                    .action(action)
                    .resource(resource)
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            // Log in structured format
            log.info("AUDIT_EVENT - {}", objectMapper.writeValueAsString(auditLog));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log details", e);
            // Fallback to simple logging
            log.info("AUDIT_EVENT - User: {} performed {} on resource: {} with details: {}", 
                    userId, action, resource, details);
        }
    }

    /**
     * Log security-related events
     */
    public void logSecurityEvent(String userId, String eventType, String ip, String description) {
        try {
            SecurityLogEntry securityLog = SecurityLogEntry.builder()
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .userId(userId)
                    .eventType(eventType)
                    .ipAddress(ip)
                    .description(description)
                    .build();

            log.warn("SECURITY_EVENT - {}", objectMapper.writeValueAsString(securityLog));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize security log", e);
            log.warn("SECURITY_EVENT - User: {} - Type: {} - IP: {} - Description: {}", 
                    userId, eventType, ip, description);
        }
    }

    /**
     * Log system events
     */
    public void logSystemEvent(String component, String event, Map<String, Object> metadata) {
        try {
            SystemLogEntry systemLog = SystemLogEntry.builder()
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .component(component)
                    .event(event)
                    .metadata(objectMapper.writeValueAsString(metadata))
                    .build();

            log.info("SYSTEM_EVENT - {}", objectMapper.writeValueAsString(systemLog));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize system log", e);
            log.info("SYSTEM_EVENT - Component: {} - Event: {} - Metadata: {}", 
                    component, event, metadata);
        }
    }

    /**
     * Log data access events
     */
    public void logDataAccess(String userId, String operation, String resource, String accessLevel) {
        Map<String, String> details = Map.of(
            "operation", operation,
            "resource", resource,
            "accessLevel", accessLevel
        );
        
        log.info("DATA_ACCESS - User {} performed {} on {} with level {}", 
                userId, operation, resource, accessLevel);
    }

    /**
     * Log business transaction
     */
    public void logBusinessTransaction(String transactionId, String userId, String operation, 
                                     Object beforeState, Object afterState) {
        try {
            BusinessTransactionLog logEntry = BusinessTransactionLog.builder()
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .transactionId(transactionId)
                    .userId(userId)
                    .operation(operation)
                    .beforeState(objectMapper.writeValueAsString(beforeState))
                    .afterState(objectMapper.writeValueAsString(afterState))
                    .build();

            log.info("BUSINESS_TRANSACTION - {}", objectMapper.writeValueAsString(logEntry));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize business transaction log", e);
            log.info("BUSINESS_TRANSACTION - ID: {} - User: {} - Operation: {}", 
                    transactionId, userId, operation);
        }
    }

    // Inner classes for audit log entries
    public static class AuditLogEntry {
        private String timestamp;
        private String userId;
        private String action;
        private String resource;
        private String details;

        public static AuditLogEntryBuilder builder() {
            return new AuditLogEntryBuilder();
        }

        public static class AuditLogEntryBuilder {
            private String timestamp;
            private String userId;
            private String action;
            private String resource;
            private String details;

            public AuditLogEntryBuilder timestamp(String timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public AuditLogEntryBuilder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public AuditLogEntryBuilder action(String action) {
                this.action = action;
                return this;
            }

            public AuditLogEntryBuilder resource(String resource) {
                this.resource = resource;
                return this;
            }

            public AuditLogEntryBuilder details(String details) {
                this.details = details;
                return this;
            }

            public AuditLogEntry build() {
                AuditLogEntry entry = new AuditLogEntry();
                entry.timestamp = this.timestamp;
                entry.userId = this.userId;
                entry.action = this.action;
                entry.resource = this.resource;
                entry.details = this.details;
                return entry;
            }
        }
    }

    public static class SecurityLogEntry {
        private String timestamp;
        private String userId;
        private String eventType;
        private String ipAddress;
        private String description;

        public static SecurityLogEntryBuilder builder() {
            return new SecurityLogEntryBuilder();
        }

        public static class SecurityLogEntryBuilder {
            private String timestamp;
            private String userId;
            private String eventType;
            private String ipAddress;
            private String description;

            public SecurityLogEntryBuilder timestamp(String timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public SecurityLogEntryBuilder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public SecurityLogEntryBuilder eventType(String eventType) {
                this.eventType = eventType;
                return this;
            }

            public SecurityLogEntryBuilder ipAddress(String ipAddress) {
                this.ipAddress = ipAddress;
                return this;
            }

            public SecurityLogEntryBuilder description(String description) {
                this.description = description;
                return this;
            }

            public SecurityLogEntry build() {
                SecurityLogEntry entry = new SecurityLogEntry();
                entry.timestamp = this.timestamp;
                entry.userId = this.userId;
                entry.eventType = this.eventType;
                entry.ipAddress = this.ipAddress;
                entry.description = this.description;
                return entry;
            }
        }
    }

    public static class SystemLogEntry {
        private String timestamp;
        private String component;
        private String event;
        private String metadata;

        public static SystemLogEntryBuilder builder() {
            return new SystemLogEntryBuilder();
        }

        public static class SystemLogEntryBuilder {
            private String timestamp;
            private String component;
            private String event;
            private String metadata;

            public SystemLogEntryBuilder timestamp(String timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public SystemLogEntryBuilder component(String component) {
                this.component = component;
                return this;
            }

            public SystemLogEntryBuilder event(String event) {
                this.event = event;
                return this;
            }

            public SystemLogEntryBuilder metadata(String metadata) {
                this.metadata = metadata;
                return this;
            }

            public SystemLogEntry build() {
                SystemLogEntry entry = new SystemLogEntry();
                entry.timestamp = this.timestamp;
                entry.component = this.component;
                entry.event = this.event;
                entry.metadata = this.metadata;
                return entry;
            }
        }
    }

    public static class BusinessTransactionLog {
        private String timestamp;
        private String transactionId;
        private String userId;
        private String operation;
        private String beforeState;
        private String afterState;

        public static BusinessTransactionLogBuilder builder() {
            return new BusinessTransactionLogBuilder();
        }

        public static class BusinessTransactionLogBuilder {
            private String timestamp;
            private String transactionId;
            private String userId;
            private String operation;
            private String beforeState;
            private String afterState;

            public BusinessTransactionLogBuilder timestamp(String timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public BusinessTransactionLogBuilder transactionId(String transactionId) {
                this.transactionId = transactionId;
                return this;
            }

            public BusinessTransactionLogBuilder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public BusinessTransactionLogBuilder operation(String operation) {
                this.operation = operation;
                return this;
            }

            public BusinessTransactionLogBuilder beforeState(String beforeState) {
                this.beforeState = beforeState;
                return this;
            }

            public BusinessTransactionLogBuilder afterState(String afterState) {
                this.afterState = afterState;
                return this;
            }

            public BusinessTransactionLog build() {
                BusinessTransactionLog entry = new BusinessTransactionLog();
                entry.timestamp = this.timestamp;
                entry.transactionId = this.transactionId;
                entry.userId = this.userId;
                entry.operation = this.operation;
                entry.beforeState = this.beforeState;
                entry.afterState = this.afterState;
                return entry;
            }
        }
    }
}