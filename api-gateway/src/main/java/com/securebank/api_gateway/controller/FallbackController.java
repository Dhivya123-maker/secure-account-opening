package com.securebank.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    private Mono<ResponseEntity<Map<String, Object>>> fallbackResponse(String service) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", service);
        response.put("message", service + " is currently unavailable. Please try again later.");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @RequestMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return fallbackResponse("Auth Service");
    }

    @RequestMapping("/fallback/customer")
    public Mono<ResponseEntity<Map<String, Object>>> customerFallback() {
        return fallbackResponse("Customer Service");
    }

    @RequestMapping("/fallback/account")
    public Mono<ResponseEntity<Map<String, Object>>> accountFallback() {
        return fallbackResponse("Account Service");
    }

    @RequestMapping("/fallback/document")
    public Mono<ResponseEntity<Map<String, Object>>> documentFallback() {
        return fallbackResponse("Document Service");
    }

    @RequestMapping("/fallback/notification")
    public Mono<ResponseEntity<Map<String, Object>>> notificationFallback() {
        return fallbackResponse("Notification Service");
    }
}
