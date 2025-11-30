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
    private final TenantResolverInterceptor tenantResolverInterceptor;
    private final CorrelationIdInterceptor correlationIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Tambahkan correlation ID interceptor pertama agar bisa digunakan di interceptor lain
        registry.addInterceptor(correlationIdInterceptor)
                .addPathPatterns("/api/v1/**") // Apply to all API endpoints
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints

        // Tambahkan tenant resolver interceptor
        registry.addInterceptor(tenantResolverInterceptor)
                .addPathPatterns("/api/v1/**") // Apply to all API endpoints
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints

        // Kemudian tambahkan request tracing interceptor
        registry.addInterceptor(requestTracingUtil)
                .addPathPatterns("/api/v1/**") // Apply to all API endpoints
                .excludePathPatterns("/actuator/**"); // Exclude actuator endpoints
    }
}