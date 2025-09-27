package com.example.travelagency.config;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class LoggingRetryer extends Retryer.Default {
    private static final Logger log = LoggerFactory.getLogger(LoggingRetryer.class);

    public LoggingRetryer() {
        // period = 200 ms, maxPeriod = 1000 ms, maxAttempts = 3
        super(200, 1000, 3);
        log.info(">>> LoggingRetryer registrado <<<");
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.warn("[Feign-Retry] Reintentando {} {} tras fallo: {}", e.request().httpMethod(), e.request().url(), e.getMessage());
        super.continueOrPropagate(e);
    }
}
