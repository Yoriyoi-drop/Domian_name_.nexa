package com.myproject.nexa.services;

import java.util.concurrent.TimeUnit;

public interface RateLimitService {
    boolean isAllowed(String key, int limit, long window, TimeUnit unit);
    boolean isAllowed(String key, String identifier, int limit, long window, TimeUnit unit);
    long getRemainingRequests(String key, long window, TimeUnit unit);
    long getResetTime(String key, long window, TimeUnit unit);
    void resetLimit(String key);

    // Additional methods for RateLimitFilter
    boolean isIpAllowed(String ip);
    long getRemainingRequests(String key);
    long getResetTime(String key);
}