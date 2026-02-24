package com.securebank.document_service.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long documentId;
    private Long customerId;
    private String documentType;
    private String documentNumber;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String documentStatus;
    private String rejectionReason;
    private LocalDate expiryDate;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}