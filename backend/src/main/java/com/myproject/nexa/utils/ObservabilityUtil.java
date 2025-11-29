package com.myproject.nexa.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Comprehensive observability utilities for metrics and performance tracking
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ObservabilityUtil {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();

    /**
     * Record a business metric
     */
    public void recordBusinessMetric(String metricName, String operation, double value, String... tags) {
        try {
            List<Tag> metricTags = buildTags("operation", operation);
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    metricTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Counter.builder(metricName)
                    .description("Business metric for " + metricName)
                    .tags(metricTags)
                    .register(meterRegistry)
                    .increment(value);
        } catch (Exception e) {
            log.warn("Failed to record business metric: {}", metricName, e);
        }
    }

    /**
     * Time an operation and record metrics
     */
    public <T> T timeOperation(String operationName, Supplier<T> operation, String... tags) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            T result = operation.get();

            List<Tag> metricTags = buildTags("operation", operationName);
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    metricTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Timer timer = Timer.builder("operation.duration")
                    .description("Duration of " + operationName)
                    .tags(metricTags)
                    .register(meterRegistry);

            sample.stop(timer);

            return result;
        } catch (Exception e) {
            List<Tag> errorTags = buildTags("operation", operationName, "status", "error");
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    errorTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Timer timer = Timer.builder("operation.duration")
                    .description("Duration of " + operationName)
                    .tags(errorTags)
                    .register(meterRegistry);
            sample.stop(timer);

            throw e;
        }
    }

    private List<Tag> buildTags(String... tagPairs) {
        if (tagPairs.length % 2 != 0) {
            throw new IllegalArgumentException("Tag pairs must be even");
        }

        List<Tag> tags = new java.util.ArrayList<>();
        for (int i = 0; i < tagPairs.length; i += 2) {
            if (i + 1 < tagPairs.length) {
                tags.add(Tag.of(tagPairs[i], tagPairs[i + 1]));
            }
        }
        return tags;
    }

    /**
     * Record an event
     */
    public void recordEvent(String eventType, String operation, String... tags) {
        try {
            List<Tag> eventTags = buildTags("type", eventType, "operation", operation);
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    eventTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Counter.builder("event.count")
                    .description("Count of events")
                    .tags(eventTags)
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            log.warn("Failed to record event: {}", eventType, e);
        }
    }

    /**
     * Record error metrics
     */
    public void recordError(String operation, String errorType, String... tags) {
        try {
            List<Tag> errorTags = buildTags("operation", operation, "type", errorType);
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    errorTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Counter.builder("error.count")
                    .description("Count of errors")
                    .tags(errorTags)
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            log.warn("Failed to record error: {}", operation, e);
        }
    }

    /**
     * Record performance metrics
     */
    public void recordPerformance(String operation, long durationMs, String... tags) {
        try {
            List<Tag> perfTags = buildTags("operation", operation);
            for (int i = 0; i < tags.length; i += 2) {
                if (i + 1 < tags.length) {
                    perfTags.add(Tag.of(tags[i], tags[i + 1]));
                }
            }

            Timer.builder("performance.duration")
                    .description("Performance duration")
                    .publishPercentileHistogram()
                    .tags(perfTags)
                    .register(meterRegistry)
                    .record(java.time.Duration.ofMillis(durationMs));
        } catch (Exception e) {
            log.warn("Failed to record performance: {}", operation, e);
        }
    }

    public void recordBusinessMetric(String metricName, String operation, double value) {
        recordBusinessMetric(metricName, operation, value, "operation", operation);
    }
}