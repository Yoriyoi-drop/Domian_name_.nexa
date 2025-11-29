package com.myproject.nexa.aspect;

import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Aspect for input validation and sanitization
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class InputValidationAspect {

    private final SecurityUtil securityUtil;

    @Pointcut("execution(* com.myproject.nexa.controllers.*.*(..))")
    public void controllerMethod() {}

    @Before("controllerMethod()")
    public void validateAndSanitizeInput(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            if (arg instanceof String) {
                String input = (String) arg;
                if (securityUtil.containsMaliciousContent(input)) {
                    log.warn("Malicious input detected: {}", input);
                    throw new IllegalArgumentException("Input contains potentially malicious content");
                }
            }
        }
    }
}