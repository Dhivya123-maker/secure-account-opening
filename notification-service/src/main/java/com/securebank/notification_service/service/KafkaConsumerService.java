package com.securebank.notification_service.service;

import com.securebank.notification_service.dto.event.AccountOpenedEvent;
import com.securebank.notification_service.dto.event.DocumentVerifiedEvent;
import com.securebank.notification_service.dto.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-registered",
            groupId = "notification-user-registered-v2",
            containerFactory = "userRegisteredKafkaListenerContainerFactory")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received user registered event for: {}", event.getEmail());
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("firstName", event.getUsername());
            variables.put("username", event.getUsername());
            notificationService.sendTemplatedNotification(
                    "WELCOME_EMAIL", event.getEmail(), event.getUserId(), variables);
        } catch (Exception e) {
            log.error("Failed to process user registered event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "account-opened",
            groupId = "notification-account-opened-v2",
            containerFactory = "accountOpenedKafkaListenerContainerFactory")
    public void handleAccountOpened(AccountOpenedEvent event) {
        log.info("Received account opened event for: {}", event.getEmail());
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("firstName", event.getFirstName());
            variables.put("accountNumber", event.getAccountNumber());
            variables.put("accountType", event.getAccountType());
            notificationService.sendTemplatedNotification(
                    "ACCOUNT_OPENED", event.getEmail(), event.getCustomerId(), variables);
        } catch (Exception e) {
            log.error("Failed to process account opened event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "document-verified",
            groupId = "notification-document-verified-v2",
            containerFactory = "documentVerifiedKafkaListenerContainerFactory")
    public void handleDocumentVerified(DocumentVerifiedEvent event) {
        log.info("Received document verified event for: {}", event.getEmail());
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("firstName", event.getFirstName());
            variables.put("documentType", event.getDocumentType());
            variables.put("status", event.getStatus());
            notificationService.sendTemplatedNotification(
                    "DOCUMENT_VERIFIED", event.getEmail(), event.getCustomerId(), variables);
        } catch (Exception e) {
            log.error("Failed to process document verified event: {}", e.getMessage());
        }
    }
}