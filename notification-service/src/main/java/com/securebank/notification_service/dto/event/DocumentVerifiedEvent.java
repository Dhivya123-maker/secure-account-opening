package com.securebank.notification_service.dto.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerifiedEvent {
    private Long customerId;
    private String documentType;
    private String status;
    private String email;
    private String firstName;
}