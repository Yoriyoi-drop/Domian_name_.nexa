package com.myproject.nexa.config;

import com.myproject.nexa.utils.RequestTracingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestTracingUtil requestTracingUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTracingUtil)
                .addPathPatterns("/api/v1/**") // Apply to all API endpoints
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints to avoid circular logging
    }
}