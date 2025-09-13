package com.school.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.config.DateTimeUtils;

@RestController
@RequestMapping("/api/time")
public class TimeController {
    
    @GetMapping("/check")
    public Map<String, String> checkTimeZone() {
        Map<String, String> response = new HashMap<>();
        response.put("serverTime", LocalDateTime.now().toString());
        response.put("appTime", DateTimeUtils.now().toString());
        response.put("timeZone", "America/Guatemala");
        response.put("systemDefaultTimeZone", TimeZone.getDefault().getID());
        return response;
    }
}