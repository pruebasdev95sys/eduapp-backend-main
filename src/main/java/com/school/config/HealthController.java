package com.school.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK - " + new Date();
    }

    @GetMapping("/api/health")
    public String apiHealthCheck() {
        return "{\"status\": \"UP\", \"timestamp\": \"" + new Date() + "\"}";
    }
}