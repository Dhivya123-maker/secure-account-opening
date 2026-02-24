package com.securebank.notification_service.service;

import com.securebank.notification_service.dto.request.NotificationRequest;
import com.securebank.notification_service.dto.response.NotificationResponse;
import com.securebank.notification_service.entity.Notification;
import com.securebank.notification_service.entity.NotificationTemplate;
import com.securebank.notification_service.repository.NotificationRepository;
import com.securebank.notification_service.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .customerId(request.getCustomerId())
                .notificationType(request.getNotificationType())
                .channel(request.getChannel())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status("PENDING")
                .build();

        notification = notificationRepository.save(notification);

        try {
            if ("EMAIL".equals(request.getChannel())) {
                emailService.sendEmail(request.getRecipient(),
                        request.getSubject(), request.getMessage());
            }
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            log.error("Failed to send notification: {}", e.getMessage());
        }

        notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    @Transactional
    public void sendTemplatedNotification(String templateCode, String recipient,
                                          Long customerId, Map<String, String> variables) {
        NotificationTemplate template = templateRepository
                .findByTemplateCodeAndIsActive(templateCode, true)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateCode));

        String body = replacePlaceholders(template.getBody(), variables);
        String subject = replacePlaceholders(template.getSubject(), variables);

        NotificationRequest request = NotificationRequest.builder()
                .customerId(customerId)
                .notificationType(templateCode)
                .channel(template.getChannel())
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .build();

        sendNotification(request);
    }

    public List<NotificationResponse> getNotificationsByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationResponse> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String replacePlaceholders(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .customerId(notification.getCustomerId())
                .notificationType(notification.getNotificationType())
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .status(notification.getStatus())
                .retryCount(notification.getRetryCount())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}