package com.securebank.document_service.service;

import com.securebank.document_service.dto.event.DocumentVerifiedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDocumentVerifiedEvent(DocumentVerifiedEvent event) {
        try {
            kafkaTemplate.send("document-verified", event);
            log.info("Published document-verified event for customer: {}",
                    event.getCustomerId());
        } catch (Exception e) {
            log.error("Failed to publish document-verified event: {}", e.getMessage());
        }
    }
}