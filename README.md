# Mini Banking — Open Banking Consent Management System

A robust, enterprise-grade mini core banking engine built with **Java 17**, **Spring Boot**, and **PostgreSQL**. This system serves as the foundational backbone for an **Open Banking Consent Management** platform, following standard PSD2 / Open Banking specifications.

---

## 📑 Table of Contents
- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Database Setup](#-database-setup)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Error Handling](#-error-handling)
- [6-Week Roadmap](#-6-week-roadmap)

---

## 🎯 Overview

In modern Open Banking, third-party fintech apps (TPPs) cannot directly access user credentials. Instead, customers explicitly grant fine-grained, revocable **consents** for account information and payment initiation. 

This project implements:
1. **Core Banking Engine**: Customers, Bank Accounts, Transactions, and Beneficiaries.
2. **Open Banking Consent Engine**: Secure authorization flows, token verification, and consent lifecycles.

---

## 🏗 Architecture

The backend adheres strictly to the **Controller → Service → Repository** layered pattern:

```
                  HTTP Request
                       │
                       ▼
         ┌───────────────────────────┐
         │  Controller Layer         │  REST Endpoints & @Valid
         │  (controller/)            │
         └─────────────┬─────────────┘
                       │
                       ▼
         ┌───────────────────────────┐
         │  Service Layer            │  Business Logic, Rule Validation,
         │  (service/)               │  DTO ↔ Entity Mapping
         └─────────────┬─────────────┘
                       │
                       ▼
         ┌───────────────────────────┐
         │  Repository Layer         │  Spring Data JPA
         │  (repository/)            │  (PostgreSQL Dialect)
         └─────────────┬─────────────┘
                       │
                       ▼
               PostgreSQL Database
```

### Project Structure
```text
mini-banking/
├── src/main/java/com/banfico/mini_banking/
│   ├── MiniBankingApplication.java      # Application entry point
│   ├── controller/                      # REST Controllers (Health, Info, Customer)
│   ├── service/                         # Business logic services
│   ├── repository/                      # Spring Data JPA repositories
│   ├── entity/                          # Hibernate / JPA database entities
│   ├── dto/response/                    # Request and Response record DTOs
│   ├── exception/                       # Custom exceptions and GlobalExceptionHandler
│   └── config/                          # Configuration beans
└── src/main/resources/
    └── application.properties           # Database & server configuration
```

---

## 💻 Tech Stack

- **Java**: 17 LTS
- **Framework**: Spring Boot 4.x (Web, Data JPA, Validation, DevTools, Actuator)
- **Database**: PostgreSQL 15+
- **ORM / Persistence**: Hibernate / JPA
- **Build Tool**: Maven (`mvnw` wrapper included)
- **Containerization**: Docker (PostgreSQL)

---

## ⚙️ Prerequisites

Ensure the following tools are installed on your system:
- **JDK 17** (`java -version`)
- **Maven** (or use the included `./mvnw.cmd`)
- **PostgreSQL** or **Docker Desktop**
- **Postman** (or curl) for API testing

---

## 🗄 Database Setup

### Option 1: Using Docker (Recommended)
Run PostgreSQL in a detached container:
```bash
docker run --name mini-banking-db \
  -e POSTGRES_DB=mini_banking \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=jeevan123 \
  -p 5432:5432 \
  -d postgres:15
```

Manage container:
```bash
docker start mini-banking-db   # Start container
docker stop mini-banking-db    # Stop container
docker ps                      # Verify running container
```

### Option 2: Local PostgreSQL
Open `psql` or **pgAdmin 4** and run:
```sql
CREATE DATABASE mini_banking;
```

Verify your credentials match `mini-banking/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mini_banking
spring.datasource.username=postgres
spring.datasource.password=jeevan123
spring.jpa.hibernate.ddl-auto=update
```

---

## 🚀 Running the Application

Navigate to the `mini-banking` directory and execute:

```powershell
cd mini-banking
.\mvnw.cmd spring-boot:run
```

The application starts by default on port **`8080`**.

---

## 📡 API Documentation

### 1. System & Health Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/health` | System health check (Map response) |
| `GET` | `/health-2` | Typed health check using DTO record |
| `GET` | `/info` | Application metadata |
| `GET` | `/info-2` | Typed application metadata using DTO record |

#### Sample Health Response
```json
{
  "status": "UP",
  "service": "mini-banking"
}
```

---

### 2. Customer Management Endpoints (`/api/customer`)

| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/customer` | Register a new customer | `201 Created` |
| `GET` | `/api/customer` | Retrieve all customers | `200 OK` |
| `GET` | `/api/customer/{id}` | Get customer by ID | `200 OK` |

#### Create Customer Request (`POST /api/customer`)
```json
{
  "firstName": "Jeevan",
  "lastName": "Kumar",
  "email": "jeevan@example.com",
  "phoneNumber": "7094011675",
  "address": "Bangalore, India"
}
```

#### Customer Response (`201 Created` / `200 OK`)
```json
{
  "id": 1,
  "firstName": "Jeevan",
  "lastName": "Kumar",
  "email": "jeevan@example.com"
}
```

---

## 🛡 Error Handling

Centralized exception handling via `@RestControllerAdvice` returns standardized error payloads:

```json
{
  "timestamp": "2026-09-10T00:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found: 99",
  "path": "/api/customer/99"
}
```

| HTTP Status | Exception | Scenario |
|---|---|---|
| `400 Bad Request` | `MethodArgumentNotValidException` | Validation failed (e.g. invalid email format) |
| `404 Not Found` | `ResourceNotFoundException` | Resource ID does not exist |
| `409 Conflict` | `DuplicateResourceException` | Unique constraint violated (e.g. duplicate email) |

---

## 🗺 6-Week Roadmap

- [x] **Week 1: Setup & Foundations** — Spring Boot initialization, PostgreSQL connection, Health/Info APIs, Git.
- [ ] **Week 2: Core Banking CRUD** — Customer, BankAccount, Transaction, and Beneficiary modules with full validation and exception handling.
- [ ] **Week 3: Frontend Integration** — Responsive customer banking dashboard with account views and transaction logs.
- [ ] **Week 4: Keycloak Identity & Security** — OAuth2 / OpenID Connect, JWT tokens, RBAC.
- [ ] **Week 5: Containerization & Gateway** — Docker Compose stack with Nginx API Gateway routing.
- [ ] **Week 6: Open Banking Consent Engine** — Full consent lifecycle (Request, Authorize, Revoke, Enforce).
