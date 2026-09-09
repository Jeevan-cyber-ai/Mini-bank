package com.banfico.mini_banking.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("name", "Mini Banking System");
        response.put("version", "1.0.0");
        response.put("description", "Open Banking Consent Management System");
        return ResponseEntity.ok(response);
    }

}
