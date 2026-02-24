🏦 Secure Account Opening Application — Full Project Roadmap
Stack: Spring Boot 3 · Angular 17 · Oracle · Kafka · RabbitMQ · Eureka · Spring Cloud · API Gateway · Docker · Helm · AWS · CI/CD · Grafana · GitHub

📦 Microservices Breakdown
Service	Port	Responsibility	DB
config-server	8071	Centralized config via GitHub	—
eureka-server	8070	Service discovery & registry	—
api-gateway	8072	Routing, auth filter, rate limiting	—
auth-service	8080	JWT issuance, OAuth2, user login	Oracle
customer-service	8081	Customer registration, KYC, profile	Oracle
account-service	8082	Account creation, types, status	Oracle
document-service	8083	ID upload, verification, S3 storage	Oracle
notification-service	8084	Email/SMS via Kafka + RabbitMQ	Oracle
🗺️ Phase-by-Phase Build Plan

✅ PHASE 1 — Project Scaffolding & Config Server
Goal: Set up the entire project structure, parent POM, Config Server backed by GitHub, and Eureka Server.
Deliverables:
* Maven multi-module parent pom.xml
* config-server (Spring Cloud Config)
* eureka-server (Netflix Eureka)
* GitHub config repo structure
* application.yml per service in config repo
* Docker Compose baseline
Key Dependencies: spring-cloud-config-server, spring-cloud-netflix-eureka-server

✅ PHASE 2 — Auth Service (JWT + OAuth2 + Spring Security)
Goal: Full authentication and authorization with JWT tokens, refresh tokens, role-based access.
Deliverables:
* auth-service Spring Boot app
* JWT token generation & validation
* Refresh token rotation
* Oracle schema: users, roles, refresh_tokens
* PL/SQL: sp_create_user, sp_validate_token
* REST APIs: POST /auth/register, POST /auth/login, POST /auth/refresh, POST /auth/logout
* Spring Security config with SecurityFilterChain
Key Dependencies: spring-security, jjwt, spring-boot-starter-oauth2-resource-server

✅ PHASE 3 — Customer Service
Goal: Full customer lifecycle — registration, KYC status, profile management.
Deliverables:
* customer-service Spring Boot app
* Oracle schema: customers, kyc_details, addresses
* PL/SQL: sp_create_customer, sp_update_kyc_status, sp_get_customer_profile
* REST APIs: POST /customers, GET /customers/{id}, PUT /customers/{id}/kyc
* Feign Client to Auth Service for token validation
* Kafka producer: customer-registered event
* Bean Validation (@Valid, custom validators)
Key Dependencies: spring-cloud-openfeign, spring-kafka, oracle-jdbc

✅ PHASE 4 — Account Service
Goal: Account creation workflows, account types, status transitions.
Deliverables:
* account-service Spring Boot app
* Oracle schema: accounts, account_types, account_status_history
* PL/SQL: sp_open_account, sp_close_account, sp_get_account_details, fn_generate_account_number
* REST APIs: POST /accounts, GET /accounts/{id}, PUT /accounts/{id}/status, GET /accounts/customer/{customerId}
* Kafka consumer: listens to customer-registered event → auto-creates default savings account
* RabbitMQ producer: sends account-opened task to notification queue
* Optimistic locking with @Version
Key Dependencies: spring-amqp, spring-kafka

✅ PHASE 5 — Document Service (KYC Upload)
Goal: Secure document upload, storage in AWS S3, verification workflow.
Deliverables:
* document-service Spring Boot app
* Oracle schema: documents, document_types, verification_log
* AWS S3 integration for file storage
* REST APIs: POST /documents/upload, GET /documents/{id}, PUT /documents/{id}/verify
* Pre-signed URL generation for secure download
* Virus scan hook (ClamAV integration point)
* Kafka event: document-verified
Key Dependencies: aws-java-sdk-s3, spring-kafka, tika (file type detection)

✅ PHASE 6 — Notification Service (Kafka + RabbitMQ)
Goal: Event-driven notifications via email and SMS triggered by Kafka and RabbitMQ.
Deliverables:
* notification-service Spring Boot app
* Kafka consumers: customer-registered, document-verified, account-opened
* RabbitMQ consumers: notification.queue
* Email via JavaMailSender (AWS SES)
* SMS via Twilio (pluggable)
* Oracle schema: notification_log
* Retry + DLQ (Dead Letter Queue) configuration
* HTML email templates (Thymeleaf)
Key Dependencies: spring-kafka, spring-amqp, spring-boot-starter-mail, thymeleaf

✅ PHASE 7 — API Gateway + Security Filter
Goal: Single entry point with JWT validation, routing, rate limiting, CORS.
Deliverables:
* api-gateway Spring Cloud Gateway app
* JWT Auth Filter (GlobalFilter)
* Route configuration for all services
* Rate limiting via Redis
* CORS configuration
* Circuit Breaker via Resilience4j
* Request/Response logging filter
* Swagger aggregation (SpringDoc)
Key Dependencies: spring-cloud-gateway, spring-cloud-circuitbreaker-resilience4j, spring-data-redis

✅ PHASE 8 — Angular 17 Frontend
Goal: Full account opening UI with multi-step form, document upload, dashboard.
Deliverables:
* Angular 17 standalone app
* Pages: Login, Register, KYC Form, Document Upload, Account Dashboard
* Angular Guards (AuthGuard, RoleGuard)
* JWT interceptor (auto-attach token + refresh)
* Reactive Forms with validation
* Tailwind CSS styling
* Step-by-step account opening wizard
* Real-time status updates (SSE/WebSocket)
Key Dependencies: @angular/forms, @angular/router, rxjs, tailwindcss

✅ PHASE 9 — Docker + Helm + Kubernetes + AWS
Goal: Full containerization and cloud deployment on AWS EKS.
Deliverables:
* Dockerfile per service (multi-stage builds)
* docker-compose.yml for local full-stack run
* Helm charts per service (values.yaml, deployment.yaml, service.yaml, ingress.yaml)
* AWS EKS cluster setup (Terraform scripts)
* AWS RDS Oracle provisioning
* AWS MSK (Managed Kafka) setup
* AWS S3 bucket config
* Kubernetes Secrets + ConfigMaps
* Horizontal Pod Autoscaler (HPA)
* Ingress with AWS ALB Controller

✅ PHASE 10 — CI/CD + Monitoring + Observability
Goal: Automated pipelines, full observability with Grafana dashboards.
Deliverables:
* GitHub Actions workflows:
    * build.yml — build + test on PR
    * deploy-staging.yml — push to ECR + deploy to staging EKS
    * deploy-prod.yml — manual approval + prod deploy
* Prometheus scrape configs per service
* Grafana dashboards:
    * Service health overview
    * API Gateway request rates
    * Kafka consumer lag
    * JVM metrics
* Distributed tracing with Micrometer + Zipkin
* Centralized logging with AWS CloudWatch
* Alerting rules (PagerDuty/Slack webhook)


🛠️ Technology Version Matrix
Technology	Version
Java	21 (LTS)
Spring Boot	4.0.0
Spring Cloud	2025.x
Angular	17
Oracle DB	21c / 19c
Kafka	3.7
RabbitMQ	3.13
Docker	26+
Kubernetes	1.29
Helm	3.x
AWS EKS	1.29
Grafana	10.x
Prometheus	2.x
Terraform	1.8
Node.js	20 LTS
🗄️ Oracle Schema Overview
-- Auth Service
users, roles, user_roles, refresh_tokens

-- Customer Service  
customers, kyc_details, addresses, kyc_documents

-- Account Service
accounts, account_types, account_status_history, account_products

-- Document Service
documents, document_types, verification_log

-- Notification Service
notification_log, notification_templates

🔐 Security Architecture
Request → API Gateway
            ↓
     JWT Auth Filter
     (validate signature, expiry, roles)
            ↓
     Route to Service
            ↓
     Service-level @PreAuthorize
     (method security)
            ↓
     PL/SQL Row-Level Security
     (Oracle VPD policies)
Security layers:
1. TLS/HTTPS everywhere
2. JWT (RS256 — asymmetric signing)
3. Refresh token rotation
4. Oracle VPD (Virtual Private Database) row-level security
5. API Gateway rate limiting
6. Input validation at every layer
7. OWASP-aligned security headers

🚀 Build Order (Dependency Graph)
1. config-server        (no dependencies)
2. eureka-server        (depends on: config-server)
3. auth-service         (depends on: config-server, eureka-server, oracle)
4. customer-service     (depends on: auth-service, kafka, oracle)
5. account-service      (depends on: customer-service, kafka, rabbitmq, oracle)
6. document-service     (depends on: auth-service, s3, kafka, oracle)
7. notification-service (depends on: kafka, rabbitmq, oracle)
8. api-gateway          (depends on: all services, redis)
9. frontend             (depends on: api-gateway)
10. DevOps & Monitoring  (wraps everything)

✅ Phase Completion Checklist
* [ ] Phase 1 — Config Server + Eureka
* [ ] Phase 2 — Auth Service (JWT + OAuth2)
* [ ] Phase 3 — Customer Service
* [ ] Phase 4 — Account Service
* [ ] Phase 5 — Document Service
* [ ] Phase 6 — Notification Service
* [ ] Phase 7 — API Gateway
* [ ] Phase 8 — Angular Frontend
* [ ] Phase 9 — Docker + Helm + AWS
* [ ] Phase 10 — CI/CD + Grafana
