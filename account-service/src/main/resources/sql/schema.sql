-- ============================================================
-- ACCOUNT SERVICE SCHEMA
-- ============================================================

CREATE TABLE accounts (
                          account_id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          account_number      VARCHAR2(20)  NOT NULL UNIQUE,
                          customer_id         NUMBER        NOT NULL,
                          account_type        VARCHAR2(30)  NOT NULL,
                          account_status      VARCHAR2(20)  DEFAULT 'PENDING',
                          balance             NUMBER(15,2)  DEFAULT 0,
                          currency            VARCHAR2(5)   DEFAULT 'INR',
                          branch_code         VARCHAR2(10),
                          ifsc_code           VARCHAR2(15),
                          opening_date        DATE,
                          closing_date        DATE,
                          interest_rate       NUMBER(5,2),
                          overdraft_limit     NUMBER(15,2)  DEFAULT 0,
                          created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                          updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                          created_by          VARCHAR2(50),
                          updated_by          VARCHAR2(50)
);

CREATE TABLE account_transactions (
                                      transaction_id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      transaction_ref     VARCHAR2(50)  NOT NULL UNIQUE,
                                      account_id          NUMBER        NOT NULL,
                                      transaction_type    VARCHAR2(20)  NOT NULL,
                                      amount              NUMBER(15,2)  NOT NULL,
                                      balance_before      NUMBER(15,2)  NOT NULL,
                                      balance_after       NUMBER(15,2)  NOT NULL,
                                      description         VARCHAR2(500),
                                      status              VARCHAR2(20)  DEFAULT 'SUCCESS',
                                      transaction_date    TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                      created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                      CONSTRAINT fk_at_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

CREATE TABLE account_nominees (
                                  nominee_id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  account_id          NUMBER        NOT NULL,
                                  nominee_name        VARCHAR2(100) NOT NULL,
                                  relationship        VARCHAR2(50)  NOT NULL,
                                  date_of_birth       DATE,
                                  share_percentage    NUMBER(5,2)   DEFAULT 100,
                                  created_at          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                  updated_at          TIMESTAMP     DEFAULT SYSTIMESTAMP,
                                  CONSTRAINT fk_an_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

CREATE SEQUENCE account_seq
    START WITH 100000000001
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

COMMIT;