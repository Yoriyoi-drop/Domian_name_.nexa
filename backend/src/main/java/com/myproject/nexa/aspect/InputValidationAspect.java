package com.myproject.nexa.aspect;

import com.myproject.nexa.utils.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Aspect for input validation and sanitization
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class InputValidationAspect {

    private final InputSanitizer inputSanitizer;

    @Around("execution(* com.myproject.nexa.controllers..*(..))")
    public Object sanitizeInput(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                args[i] = sanitizeObject(args[i]);
            }
        }

        return joinPoint.proceed(args);
    }

    private Object sanitizeObject(Object obj) {
        if (obj == null) {
            return null;
        }

        // If it's a string, sanitize it directly
        if (obj instanceof String) {
            return inputSanitizer.sanitizePlainText((String) obj);
        }

        // If it's a custom object, sanitize its fields
        if (!isPrimitiveOrWrapper(obj.getClass()) && !(obj instanceof String)) {
            sanitizeObjectFields(obj);
        }

        return obj;
    }

    private void sanitizeObjectFields(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value instanceof String) {
                    String sanitizedValue = inputSanitizer.sanitizePlainText((String) value);
                    field.set(obj, sanitizedValue);
                }
                // Add more type-specific sanitization here if needed
            } catch (IllegalAccessException e) {
                log.warn("Could not access field {} in class {}", field.getName(), clazz.getSimpleName(), e);
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class ||
                clazz == String.class;
    }
}