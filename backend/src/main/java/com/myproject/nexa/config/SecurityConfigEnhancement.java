package com.myproject.nexa.config;

import com.myproject.nexa.aspect.InputValidationAspect;
import com.myproject.nexa.utils.InputSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigEnhancement {

    private final InputSanitizer inputSanitizer;

    @Bean
    public InputValidationAspect inputValidationAspect() {
        return new InputValidationAspect(inputSanitizer);
    }
}