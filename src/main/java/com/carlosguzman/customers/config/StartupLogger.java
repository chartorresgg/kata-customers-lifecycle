package com.carlosguzman.customers.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final String message;
    private final String appName;
    private final String port;

    public StartupLogger(@Value("${app.environment-message}") String message,
            @Value("${spring.application.name}") String appName,
            @Value("${server.port}") String port) {
        this.message = message;
        this.appName = appName;
        this.port = port;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logEnvironment() {
        log.info("{} | app={} | puerto={}", message, appName, port);
    }

}
