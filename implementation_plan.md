# Mini Banking / Open Banking Consent Management System - Training Plan

This document outlines our step-by-step approach to learning the concepts and building the "Mini Banking / Open Banking Consent Management System" as per the GCT Training Program. We will tackle this week by week.

## Prerequisites Check
Before we begin Week 1, please confirm your comfort level with the following prerequisites:
- Java basics (Variables, OOP, Collections)
- Basic SQL & Git
- Basic HTTP & REST concepts

*If you need a quick refresher on any of these, let me know before we start.*

## Proposed Schedule & Implementation Plan

### Week 1: Setup, Git, Spring Boot, PostgreSQL Basics
**Goal**: Set up the foundation of our backend application.
- **Concepts to Learn**: Spring Boot project structure, REST API basics, Git workflow, PostgreSQL integration, basic Docker.
- **Tasks**:
  1. Initialize a Spring Boot project.
  2. Set up a local PostgreSQL database (optionally using Docker).
  3. Create basic health check REST APIs (`GET /health`, `GET /api/info`).
  4. Connect the application to PostgreSQL.
  5. Initialize a Git repository and commit our initial code.

### Week 2: Banking CRUD API
**Goal**: Build the core backend banking APIs.
- **Concepts to Learn**: Controller-Service-Repository architecture, DTOs vs Entities, Hibernate/JPA mapping, Validation, Exception Handling.
- **Tasks**:
  1. Design and implement the `Customer`, `BankAccount`, `Transaction`, and `Beneficiary` entities.
  2. Implement CRUD APIs for these entities.
  3. Add request validation and global exception handling.
  4. Test APIs using Postman.

### Week 3: Frontend Integration
**Goal**: Build a simple frontend to interact with our APIs.
- **Concepts to Learn**: HTML/CSS/JS basics, Fetch API/Axios, CORS, DOM manipulation.
- **Tasks**:
  1. Create simple HTML/JS pages.
  2. Integrate the frontend with our Spring Boot backend.
  3. Handle UI states (loading, success, error).

### Week 4: Keycloak Authentication and API Protection
**Goal**: Secure our APIs using Keycloak and JWT.
- **Concepts to Learn**: Authentication vs Authorization, JWT, OAuth2, Keycloak setup, Spring Security integration.
- **Tasks**:
  1. Set up Keycloak locally (using Docker).
  2. Configure a realm, clients, users, and roles in Keycloak.
  3. Secure the Spring Boot application to validate Keycloak JWTs.
  4. Implement role-based access control (RBAC) on our endpoints.

### Week 5: Nginx Gateway and Docker Compose
**Goal**: Containerize the entire stack and route traffic through an API Gateway.
- **Concepts to Learn**: Reverse Proxies, Nginx, Docker Compose.
- **Tasks**:
  1. Write `Dockerfile` for the Spring Boot app and frontend (if applicable).
  2. Create a `docker-compose.yml` to spin up PostgreSQL, Keycloak, Backend, and Frontend.
  3. Set up Nginx as the single entry point, routing requests to the appropriate service.

### Week 6: Final Capstone Features
**Goal**: Complete the final consent management features and review.
- **Tasks**:
  1. Implement Consent creation and approval/rejection flows.
  2. Final end-to-end testing.
  3. Code cleanup and documentation.

## User Review Required
> [!IMPORTANT]
> Please review this plan. To get started, let me know:
> 1. Do you want to use Maven or Gradle for the Spring Boot project?
> 2. Are you comfortable with the prerequisites, or should we cover some basics first?
> 3. Click **Approve** if you are ready to begin **Week 1: Setup**.
