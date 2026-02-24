package com.securebank.document_service.controller;

import com.securebank.document_service.dto.request.DocumentUploadRequest;
import com.securebank.document_service.dto.request.DocumentVerifyRequest;
import com.securebank.document_service.dto.response.ApiResponse;
import com.securebank.document_service.dto.response.DocumentResponse;
import com.securebank.document_service.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestParam Long customerId,
            @RequestParam String documentType,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String expiryDate,
            @RequestPart("file") MultipartFile file) throws IOException {

        DocumentUploadRequest request = DocumentUploadRequest.builder()
                .customerId(customerId)
                .documentType(documentType)
                .documentNumber(documentNumber)
                .expiryDate(expiryDate != null ?
                        java.time.LocalDate.parse(expiryDate) : null)
                .build();

        DocumentResponse response = documentService.uploadDocument(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded successfully", response));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(
            @PathVariable Long documentId) {
        DocumentResponse response = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(ApiResponse.success("Document fetched successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByCustomerId(
            @PathVariable Long customerId) {
        List<DocumentResponse> response = documentService.getDocumentsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Documents fetched successfully", response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByStatus(
            @PathVariable String status) {
        List<DocumentResponse> response = documentService.getDocumentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Documents fetched successfully", response));
    }

    @PatchMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<DocumentResponse>> verifyDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentVerifyRequest request) {
        DocumentResponse response = documentService.verifyDocument(documentId, request);
        return ResponseEntity.ok(ApiResponse.success("Document verified successfully", response));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }
}
