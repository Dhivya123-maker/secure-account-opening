package com.securebank.document_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerifyRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String remarks;

    @NotBlank(message = "Verified by is required")
    private String verifiedBy;

    private String email;
    private String firstName;
}