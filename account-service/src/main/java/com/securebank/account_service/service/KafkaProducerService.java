package com.securebank.account_service.service;

import com.securebank.account_service.dto.event.AccountOpenedEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "publishAccountOpenedEventFallback")
    public void publishAccountOpenedEvent(AccountOpenedEvent event) {
        try {
            kafkaTemplate.send("account-opened", event);
            log.info("Published account-opened event for: {}", event.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to publish account-opened event: {}", e.getMessage());
            throw e;
        }
    }

    public void publishAccountOpenedEventFallback(AccountOpenedEvent event, Exception e) {
        log.warn("Circuit breaker OPEN - Kafka unavailable. Account opened event skipped for: {}. Error: {}",
                event.getAccountNumber(), e.getMessage());
        // Transaction succeeds even if notification fails
    }
}
