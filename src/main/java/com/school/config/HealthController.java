package com.school.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> root() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "School Management API");
        response.put("timestamp", new Date().toString());
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        return response;
    }

    @GetMapping("/api/health")
    public Map<String, String> apiHealthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("database", "connected");
        return response;
    }
}