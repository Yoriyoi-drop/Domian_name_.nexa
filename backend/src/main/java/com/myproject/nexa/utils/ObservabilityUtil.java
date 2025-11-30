package com.myproject.nexa.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class ObservabilityUtil {

    private final MeterRegistry meterRegistry;
    private final Tracer tracer;
    
    // Business metrics
    private Counter userRegistrationCounter;
    private Counter userLoginCounter;
    private Timer apiCallTimer;
    
    public ObservabilityUtil(MeterRegistry meterRegistry, Tracer tracer) {
        this.meterRegistry = meterRegistry;
        this.tracer = tracer;
    }
    
    @PostConstruct
    public void init() {
        userRegistrationCounter = Counter.builder("user.registrations")
                .description("Number of user registrations")
                .register(meterRegistry);
                
        userLoginCounter = Counter.builder("user.logins")
                .description("Number of user logins")
                .register(meterRegistry);
                
        apiCallTimer = Timer.builder("api.calls")
                .description("API call duration")
                .register(meterRegistry);
    }
    
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    public void setCorrelationId(String correlationId) {
        MDC.put("correlationId", correlationId);
    }
    
    public String getCorrelationId() {
        return MDC.get("correlationId");
    }
    
    public void clearCorrelationId() {
        MDC.remove("correlationId");
    }
    
    public <T> T traceOperation(String operationName, Supplier<T> operation) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return operation.get();
        } finally {
            span.end();
        }
    }
    
    public void recordBusinessMetric(String metricName, String operation, double value, String... tags) {
        Counter.builder(metricName)
                .description("Business metric for " + operation)
                .tags(tags)
                .register(meterRegistry)
                .increment(value);
    }
    
    public <T> T timeOperation(String operationName, Supplier<T> operation, String... tags) {
        return Timer.builder(operationName)
                .tags(tags)
                .register(meterRegistry)
                .recordCallable(() -> operation.get());
    }
    
    public void recordError(String operationName, String errorType) {
        Counter.builder("errors")
                .description("Error counter")
                .tag("operation", operationName)
                .tag("type", errorType)
                .register(meterRegistry)
                .increment();
    }
    
    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
    }
    
    public void incrementUserLogin() {
        userLoginCounter.increment();
    }
}