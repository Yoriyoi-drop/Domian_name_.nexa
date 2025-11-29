package com.myproject.nexa.config;

import com.myproject.nexa.aspect.InputValidationAspect;
import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigEnhancement {

    private final SecurityUtil securityUtil;

    @Bean
    public InputValidationAspect inputValidationAspect() {
        return new InputValidationAspect(securityUtil);
    }
}