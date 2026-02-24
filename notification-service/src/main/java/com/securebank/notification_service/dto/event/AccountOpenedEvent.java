package com.securebank.notification_service.dto.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOpenedEvent {
    private Long customerId;
    private String accountNumber;
    private String accountType;
    private String email;
    private String firstName;
}