package com.securebank.document_service.repository;

import com.securebank.document_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByCustomerId(Long customerId);

    List<Document> findByCustomerIdAndDocumentType(Long customerId, String documentType);

    List<Document> findByDocumentStatus(String documentStatus);
}