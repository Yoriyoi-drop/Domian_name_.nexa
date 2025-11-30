package com.myproject.nexa.config;

import com.myproject.nexa.security.RateLimitingService;
import com.myproject.nexa.security.JwtAuthenticationFilter;
import com.myproject.nexa.security.JwtAuthenticationEntryPoint;
import com.myproject.nexa.security.RateLimitFilter;
import com.myproject.nexa.security.RequestSanitizationFilter;
import com.myproject.nexa.security.TenantSecurityFilter;
import com.myproject.nexa.services.RateLimitService;
import com.myproject.nexa.config.properties.AppProperties;
import com.myproject.nexa.utils.InputSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestSanitizationFilter requestSanitizationFilter; // For request sanitization
    private final TenantSecurityFilter tenantSecurityFilter; // For tenant security
    private final RateLimitingService rateLimitingService;
    private final RateLimitService rateLimitService; // For rate limit filter
    private final ObjectMapper objectMapper; // For rate limit filter
    private final AppProperties appProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased strength from default 10
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowNull(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/api/v1/auth/**", HttpMethod.POST.name()),
                    new AntPathRequestMatcher("/api/v1/public/**", HttpMethod.POST.name())
                )
                // Configure CSRF token repository for cookie-based storage
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .headers(headers -> headers
                .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()) // Prevent clickjacking
                .contentTypeOptions(contentTypeOptionsConfig -> {}) // Prevent MIME type sniffing
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true)
                    .preload(true)) // Enable HSTS
                .xssProtection(xssConfig -> xssConfig.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)) // Enable XSS protection
                .cacheControl(cacheControl -> {}) // Set cache control headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self' http://localhost:* https://localhost:*; frame-ancestors 'none';")) // Content Security Policy
                .referrerPolicy(referrerPolicyConfig -> referrerPolicyConfig.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)) // Referrer policy
            )
            .sessionManagement(sessionManagement ->
                sessionManagement.maximumSessions(1) // Max 1 session per user
                    .maxSessionsPreventsLogin(false) // Allow new login, terminate old session
                    .expiredSessionStrategy(sessionInformationExpiredEvent -> {
                        // Handle expired sessions
                        try {
                            sessionInformationExpiredEvent.getResponse().setStatus(401);
                        } catch (Exception e) {
                            // Log the error if needed
                        }
                    })
            )
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/public/**").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/actuator/**").hasRole("ADMIN") // Restrict actuator endpoints
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .addFilterBefore(requestSanitizationFilter, RateLimitFilter.class) // Add request sanitization filter first
            .addFilterBefore(tenantSecurityFilter, RequestSanitizationFilter.class) // Add tenant security filter before sanitization
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitFilter(), JwtAuthenticationFilter.class); // Add rate limiting filter

        return http.build();
    }

    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter(rateLimitService, objectMapper);
    }
}