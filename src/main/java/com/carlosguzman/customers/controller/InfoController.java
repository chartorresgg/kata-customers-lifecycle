package com.carlosguzman.customers.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlosguzman.customers.dto.AppInfo;

@RestController
@RequestMapping("/api/info")
public class InfoController {

    private final String environment;
    private final String application;
    private final int port;

    public InfoController(@Value("${app.environment}") String environment,
            @Value("${spring.application.name}") String application,
            @Value("${server.port}") int port) {
        this.environment = environment;
        this.application = application;
        this.port = port;
    }

    @GetMapping
    public AppInfo getInfo() {
        return new AppInfo(environment, application, port);
    }

}
