package com.myproject.nexa.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public CustomHealthIndicator(JdbcTemplate jdbcTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Health health() {
        // Check database connectivity
        boolean dbHealthy = checkDatabaseHealth();
        // Check Redis connectivity
        boolean redisHealthy = checkRedisHealth();
        
        Health.Builder healthBuilder = new Health.Builder();
        
        if (dbHealthy && redisHealthy) {
            healthBuilder.up()
                .withDetail("database", "Database connection OK")
                .withDetail("redis", "Redis connection OK");
        } else {
            healthBuilder.down()
                .withDetail("database", dbHealthy ? "Database connection OK" : "Database connection FAILED")
                .withDetail("redis", redisHealthy ? "Redis connection OK" : "Redis connection FAILED");
        }
        
        return healthBuilder.build();
    }
    
    private boolean checkDatabaseHealth() {
        try {
            // Execute a simple query to test database connectivity
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean checkRedisHealth() {
        try {
            // Execute a simple command to test Redis connectivity
            String result = (String) redisTemplate.opsForValue().get("health_check_test_key");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}