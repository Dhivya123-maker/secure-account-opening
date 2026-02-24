-- ============================================================
-- AUTH SERVICE SCHEMA
-- ============================================================

-- USERS TABLE
CREATE TABLE users (
                       user_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username        VARCHAR2(50)  NOT NULL UNIQUE,
                       email           VARCHAR2(100) NOT NULL UNIQUE,
                       password        VARCHAR2(255) NOT NULL,
                       is_active       NUMBER(1)     DEFAULT 1 NOT NULL,
                       is_locked       NUMBER(1)     DEFAULT 0 NOT NULL,
                       created_at      TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                       updated_at      TIMESTAMP     DEFAULT SYSTIMESTAMP,
                       created_by      VARCHAR2(50),
                       updated_by      VARCHAR2(50)
);

-- ROLES TABLE
CREATE TABLE roles (
                       role_id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       role_name       VARCHAR2(50)  NOT NULL UNIQUE,
                       description     VARCHAR2(200)
);

-- USER ROLES TABLE
CREATE TABLE user_roles (
                            user_id         NUMBER NOT NULL,
                            role_id         NUMBER NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                            CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- REFRESH TOKENS TABLE
CREATE TABLE refresh_tokens (
                                token_id        NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id         NUMBER        NOT NULL,
                                token           VARCHAR2(500) NOT NULL UNIQUE,
                                expiry_date     TIMESTAMP     NOT NULL,
                                is_revoked      NUMBER(1)     DEFAULT 0 NOT NULL,
                                created_at      TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                                CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- INSERT DEFAULT ROLES
INSERT INTO roles (role_name, description) VALUES ('ROLE_ADMIN', 'Administrator');
INSERT INTO roles (role_name, description) VALUES ('ROLE_CUSTOMER', 'Customer');
INSERT INTO roles (role_name, description) VALUES ('ROLE_AGENT', 'Bank Agent');

COMMIT;