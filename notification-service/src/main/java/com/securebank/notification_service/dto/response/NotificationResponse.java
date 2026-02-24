package com.securebank.notification_service.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private Long customerId;
    private String notificationType;
    private String channel;
    private String recipient;
    private String subject;
    private String status;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}