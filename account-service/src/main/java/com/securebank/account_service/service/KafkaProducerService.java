package com.securebank.account_service.service;

import com.securebank.account_service.dto.event.AccountOpenedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAccountOpenedEvent(AccountOpenedEvent event) {
        try {
            kafkaTemplate.send("account-opened", event);
            log.info("Published account-opened event for: {}", event.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to publish account-opened event: {}", e.getMessage());
        }
    }
}