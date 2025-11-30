package com.myproject.nexa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";
    int limit() default 10;
    long window() default 60; // in seconds
    TimeUnit unit() default TimeUnit.SECONDS;
    String message() default "Rate limit exceeded";
}