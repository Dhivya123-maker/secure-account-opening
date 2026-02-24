package com.securebank.account_service.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long accountId;
    private String accountNumber;
    private Long customerId;
    private String accountType;
    private String accountStatus;
    private BigDecimal balance;
    private String currency;
    private String branchCode;
    private String ifscCode;
    private LocalDate openingDate;
    private BigDecimal interestRate;
    private BigDecimal overdraftLimit;
    private LocalDateTime createdAt;
}