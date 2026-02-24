package com.securebank.notification_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    private Long customerId;

    @NotBlank(message = "Notification type is required")
    private String notificationType;

    @NotBlank(message = "Channel is required")
    private String channel;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String subject;

    @NotBlank(message = "Message is required")
    private String message;
}