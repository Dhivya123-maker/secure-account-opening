package com.securebank.document_service.repository;

import com.securebank.document_service.entity.DocumentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentAuditRepository extends JpaRepository<DocumentAudit, Long> {

    List<DocumentAudit> findByDocumentIdOrderByPerformedAtDesc(Long documentId);
}