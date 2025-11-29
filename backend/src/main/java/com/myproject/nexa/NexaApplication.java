package com.myproject.nexa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAspectJAutoProxy
@EnableRetry
public class NexaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexaApplication.class, args);
    }

}