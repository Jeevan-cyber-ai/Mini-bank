package com.banfico.mini_banking.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")

    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new LinkedHashMap<>(); // LinkedHashMap preserves key order
        response.put("status", "UP");
        response.put("service", "mini-banking");
        return ResponseEntity.ok(response);
    }
}
