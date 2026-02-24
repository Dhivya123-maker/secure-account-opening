package com.securebank.account_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Account type is required")
    private String accountType;

    private String branchCode;
    private String ifscCode;
    private LocalDate openingDate;

    private String nomineeName;
    private String nomineeRelationship;
    private LocalDate nomineeDateOfBirth;

    private String email;
    private String firstName;
}