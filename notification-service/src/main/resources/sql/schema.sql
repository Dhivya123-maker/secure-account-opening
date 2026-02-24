-- ============================================================
-- NOTIFICATION SERVICE SCHEMA
-- ============================================================

CREATE TABLE notifications (
                               notification_id     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               customer_id         NUMBER,
                               notification_type   VARCHAR2(50)  NOT NULL,
                               channel             VARCHAR2(20)  NOT NULL,
                               recipient           VARCHAR2(200) NOT NULL,
                               subject             VARCHAR2(500),
                               message             CLOB          NOT NULL,
                               status              VARCHAR2(20)  DEFAULT 'PENDING',
                               retry_count         NUMBER        DEFAULT 0,
                               error_message       VARCHAR2(500),
                               sent_at             TIMESTAMP,
                               created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                               updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP
);

CREATE TABLE notification_templates (
                                        template_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        template_code       VARCHAR2(50)  NOT NULL UNIQUE,
                                        template_name       VARCHAR2(100) NOT NULL,
                                        channel             VARCHAR2(20)  NOT NULL,
                                        subject             VARCHAR2(500),
                                        body                CLOB          NOT NULL,
                                        is_active           NUMBER(1)     DEFAULT 1,
                                        created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                        updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP
);

INSERT INTO notification_templates (template_code, template_name, channel, subject, body)
VALUES ('WELCOME_EMAIL', 'Welcome Email', 'EMAIL',
        'Welcome to SecureBank!',
        'Dear {firstName}, Welcome to SecureBank! Your account has been created successfully. Username: {username}');

INSERT INTO notification_templates (template_code, template_name, channel, subject, body)
VALUES ('ACCOUNT_OPENED', 'Account Opened Email', 'EMAIL',
        'Your Bank Account is Now Active!',
        'Dear {firstName}, Your {accountType} account has been opened successfully. Account Number: {accountNumber}');

INSERT INTO notification_templates (template_code, template_name, channel, subject, body)
VALUES ('DOCUMENT_VERIFIED', 'Document Verified Email', 'EMAIL',
        'Document Verification Update',
        'Dear {firstName}, Your document {documentType} has been {status}.');

INSERT INTO notification_templates (template_code, template_name, channel, subject, body)
VALUES ('TRANSACTION_ALERT', 'Transaction Alert Email', 'EMAIL',
        'Transaction Alert - SecureBank',
        'Dear {firstName}, A {transactionType} of {amount} has been processed on your account {accountNumber}. Balance: {balance}');

COMMIT;