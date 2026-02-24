package com.securebank.document_service.service;

import com.securebank.document_service.dto.event.DocumentVerifiedEvent;
import com.securebank.document_service.dto.request.DocumentUploadRequest;
import com.securebank.document_service.dto.request.DocumentVerifyRequest;
import com.securebank.document_service.dto.response.DocumentResponse;
import com.securebank.document_service.entity.Document;
import com.securebank.document_service.entity.DocumentAudit;
import com.securebank.document_service.repository.DocumentAuditRepository;
import com.securebank.document_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentAuditRepository auditRepository;
    private final KafkaProducerService kafkaProducerService;

    @Value("${document.storage.path:/tmp/securebank/documents}")
    private String storagePath;

    @Transactional
    public DocumentResponse uploadDocument(DocumentUploadRequest request,
                                           MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dirPath = Paths.get(storagePath, String.valueOf(request.getCustomerId()));
        Files.createDirectories(dirPath);
        Path filePath = dirPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        Document document = Document.builder()
                .customerId(request.getCustomerId())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .fileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .documentStatus("PENDING")
                .expiryDate(request.getExpiryDate())
                .createdBy("SYSTEM")
                .build();

        document = documentRepository.save(document);

        saveAudit(document.getDocumentId(), "UPLOAD", null, "PENDING",
                "Document uploaded", "SYSTEM");

        return mapToResponse(document);
    }

    public DocumentResponse getDocumentById(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return mapToResponse(document);
    }

    public List<DocumentResponse> getDocumentsByCustomerId(Long customerId) {
        return documentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<DocumentResponse> getDocumentsByStatus(String status) {
        return documentRepository.findByDocumentStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public DocumentResponse verifyDocument(Long documentId,
                                           DocumentVerifyRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        String oldStatus = document.getDocumentStatus();
        document.setDocumentStatus(request.getStatus());
        document.setVerifiedBy(request.getVerifiedBy());
        document.setVerifiedAt(LocalDateTime.now());
        document.setUpdatedBy(request.getVerifiedBy());

        if ("REJECTED".equals(request.getStatus())) {
            document.setRejectionReason(request.getRemarks());
        }

        documentRepository.save(document);

        saveAudit(documentId, "VERIFY", oldStatus, request.getStatus(),
                request.getRemarks(), request.getVerifiedBy());

        kafkaProducerService.publishDocumentVerifiedEvent(
                DocumentVerifiedEvent.builder()
                        .customerId(document.getCustomerId())
                        .documentType(document.getDocumentType())
                        .status(request.getStatus())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .build()
        );

        return mapToResponse(document);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file");
        }
        documentRepository.delete(document);
    }

    private void saveAudit(Long documentId, String action, String oldStatus,
                           String newStatus, String remarks, String performedBy) {
        DocumentAudit audit = DocumentAudit.builder()
                .documentId(documentId)
                .action(action)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .remarks(remarks)
                .performedBy(performedBy)
                .build();
        auditRepository.save(audit);
    }

    private DocumentResponse mapToResponse(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .customerId(document.getCustomerId())
                .documentType(document.getDocumentType())
                .documentNumber(document.getDocumentNumber())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .documentStatus(document.getDocumentStatus())
                .rejectionReason(document.getRejectionReason())
                .expiryDate(document.getExpiryDate())
                .verifiedBy(document.getVerifiedBy())
                .verifiedAt(document.getVerifiedAt())
                .createdAt(document.getCreatedAt())
                .build();
    }
}