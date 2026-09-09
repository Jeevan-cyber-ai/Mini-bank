package com.banfico.mini_banking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banfico.mini_banking.dto.response.HealthResponse;

import com.banfico.mini_banking.dto.response.AppInfoResponse;

@RestController
public class Health_dto {

    @GetMapping("/health-2")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", "mini-banking"));
    }

    @GetMapping("/info-2")
    public ResponseEntity<AppInfoResponse> info() {
        return ResponseEntity.ok(new AppInfoResponse(
                "Mini Banking System",
                "1.0.0",
                "Open Banking Consent Management System"));
    }
}
