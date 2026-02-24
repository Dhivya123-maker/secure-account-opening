package com.securebank.auth_service.service;

import com.securebank.auth_service.dto.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Publishing event in thread: {}", Thread.currentThread().getName());
        try {
            kafkaTemplate.send("user-registered", event);
            log.info("Published user-registered event for: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to publish user-registered event: {}", e.getMessage());
        }
    }
}