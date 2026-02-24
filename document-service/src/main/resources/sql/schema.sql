-- ============================================================
-- DOCUMENT SERVICE SCHEMA
-- ============================================================

CREATE TABLE documents (
                           document_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           customer_id         NUMBER        NOT NULL,
                           document_type       VARCHAR2(50)  NOT NULL,
                           document_number     VARCHAR2(100),
                           file_name           VARCHAR2(255) NOT NULL,
                           file_path           VARCHAR2(500) NOT NULL,
                           file_size           NUMBER,
                           mime_type           VARCHAR2(100),
                           document_status     VARCHAR2(20)  DEFAULT 'PENDING',
                           rejection_reason    VARCHAR2(500),
                           expiry_date         DATE,
                           verified_by         VARCHAR2(100),
                           verified_at         TIMESTAMP,
                           created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                           updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                           created_by          VARCHAR2(50),
                           updated_by          VARCHAR2(50)
);

CREATE TABLE document_audit (
                                audit_id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                document_id         NUMBER        NOT NULL,
                                action              VARCHAR2(50)  NOT NULL,
                                old_status          VARCHAR2(20),
                                new_status          VARCHAR2(20),
                                remarks             VARCHAR2(500),
                                performed_by        VARCHAR2(100),
                                performed_at        TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                CONSTRAINT fk_da_document FOREIGN KEY (document_id) REFERENCES documents(document_id)
);

COMMIT;