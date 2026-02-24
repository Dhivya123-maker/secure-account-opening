-- ============================================================
-- CUSTOMER SERVICE SCHEMA
-- ============================================================

CREATE TABLE customers (
                           customer_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           user_id             NUMBER        NOT NULL UNIQUE,
                           customer_number     VARCHAR2(20)  NOT NULL UNIQUE,
                           first_name          VARCHAR2(100) NOT NULL,
                           last_name           VARCHAR2(100) NOT NULL,
                           date_of_birth       DATE          NOT NULL,
                           gender              VARCHAR2(10),
                           nationality         VARCHAR2(50),
                           pan_number          VARCHAR2(10)  UNIQUE,
                           aadhar_number       VARCHAR2(12)  UNIQUE,
                           kyc_status          VARCHAR2(20)  DEFAULT 'PENDING',
                           customer_status     VARCHAR2(20)  DEFAULT 'ACTIVE',
                           created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                           updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                           created_by          VARCHAR2(50),
                           updated_by          VARCHAR2(50)
);

CREATE TABLE customer_contacts (
                                   contact_id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   customer_id         NUMBER        NOT NULL,
                                   email               VARCHAR2(100) NOT NULL UNIQUE,
                                   phone               VARCHAR2(15)  NOT NULL,
                                   alternate_phone     VARCHAR2(15),
                                   is_email_verified   NUMBER(1)     DEFAULT 0,
                                   is_phone_verified   NUMBER(1)     DEFAULT 0,
                                   created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                   updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                                   CONSTRAINT fk_cc_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE customer_addresses (
                                    address_id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    customer_id         NUMBER        NOT NULL,
                                    address_type        VARCHAR2(20)  NOT NULL,
                                    address_line1       VARCHAR2(200) NOT NULL,
                                    address_line2       VARCHAR2(200),
                                    city                VARCHAR2(100) NOT NULL,
                                    state               VARCHAR2(100) NOT NULL,
                                    pincode             VARCHAR2(10)  NOT NULL,
                                    country             VARCHAR2(50)  DEFAULT 'India',
                                    is_primary          NUMBER(1)     DEFAULT 0,
                                    created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                    updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                                    CONSTRAINT fk_ca_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE customer_employment (
                                     employment_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     customer_id         NUMBER        NOT NULL UNIQUE,
                                     employment_type     VARCHAR2(30)  NOT NULL,
                                     employer_name       VARCHAR2(200),
                                     designation         VARCHAR2(100),
                                     annual_income       NUMBER(15,2),
                                     years_of_experience NUMBER(3),
                                     created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                     updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                                     CONSTRAINT fk_ce_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE SEQUENCE customer_seq
    START WITH 100001
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

COMMIT;