package com.example.travelagency.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options requestOptions() {
        // connectTimeout = 500 ms, readTimeout = 500 ms
        return new Request.Options(500, 500);
    }

    @Bean
    public Retryer retryer() {
        return new LoggingRetryer();
    }
}