package com.banfico.mini_banking.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class Sample_http {
    @GetMapping("/")
    public String getHello() {
        return "Hello from Spring Boot";
    }

}
