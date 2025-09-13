package com.school.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "Application is running - " + new Date();
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/api/health")
    public String apiHealthCheck() {
        return "{\"status\": \"UP\"}";
    }
}