# Week 2: Banking CRUD APIs — Entities, Services, and Full REST APIs

## 🎯 Goal
By the end of this week, you should be able to:
- Design database entities using JPA annotations
- Understand the **Controller → Service → Repository** architecture
- Build full CRUD APIs (Create, Read, Update, Delete)
- Use **DTOs** to separate API shapes from database entities
- Add **request validation** with meaningful error messages
- Implement **global exception handling**
- Test all APIs in Postman

---

## 📋 What We're Building

Our mini-banking system needs 4 core entities:

```
┌──────────────┐       ┌──────────────────┐
│   Customer   │──1:N──│   BankAccount    │
└──────────────┘       └──────────────────┘
                              │
                             1:N
                              │
                       ┌──────────────────┐
                       │   Transaction    │
                       └──────────────────┘

┌──────────────┐
│  Beneficiary │──belongs to──▶ Customer
└──────────────┘
```

| Entity | Purpose |
|--------|---------|
| **Customer** | A person who uses the bank (name, email, phone, address) |
| **BankAccount** | A bank account belonging to a customer (account number, balance, type) |
| **Transaction** | Money movement on an account (deposit, withdrawal, transfer) |
| **Beneficiary** | A saved recipient that a customer can transfer money to |

---

## Step 1: Understand the Controller-Service-Repository Pattern

### Concept: Why 3 Layers?

In Week 1, your controllers received requests and returned static maps or DTOs directly. In a real application, you separate concerns:

```
HTTP Request
    ↓
┌─────────────────────────────────────────────────┐
│  CONTROLLER (controller/)                       │
│  • Receives HTTP request                        │
│  • Validates input (@Valid)                     │
│  • Calls the Service                            │
│  • Returns HTTP response (ResponseEntity)       │
├─────────────────────────────────────────────────┤
│  SERVICE (service/)                             │
│  • Business logic & rules                       │
│  • Example: "Does email already exist?"         │
│  • Example: "Is balance sufficient to withdraw?"│
│  • Entity ↔ DTO conversions                     │
│  • Calls Repository                             │
├─────────────────────────────────────────────────┤
│  REPOSITORY (repository/)                       │
│  • Talks to PostgreSQL                          │
│  • Extends JpaRepository<Entity, ID>            │
│  • Spring Data JPA generates SQL automatically  │
└─────────────────────────────────────────────────┘
    ↓
Database (PostgreSQL)
```

### Key Annotations for Each Layer

| Layer | Annotation | What it does |
|-------|-----------|--------------|
| Controller | `@RestController` | Marks class as a REST controller returning JSON |
| Service | `@Service` | Marks class as a business logic Spring Bean |
| Repository | `@Repository` | Marks interface for database operations |

### Dependency Injection with Constructor / Lombok
Spring wires components together via constructor injection:
```java
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    // Lombok creates constructor for final fields automatically
}
```

---

## Step 2: Create the Customer Entity

### Concept: JPA Entity
A **JPA Entity** maps a Java class to a database table.

### Key Annotations:
- `@Entity`: Marks class as a DB table
- `@Table(name = "customers")`: Customizes table name
- `@Id` & `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-increment primary key
- `@Column(nullable = false, unique = true)`: Database constraints
- `@CreationTimestamp` & `@UpdateTimestamp`: Automatic audit timestamps

### File to Create:
`src/main/java/com/banfico/mini_banking/entity/Customer.java`

```java
package com.banfico.mini_banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String address;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

---

## Step 3: Create the Customer Repository

### Concept: Spring Data JPA
Extend `JpaRepository<Customer, Long>`. You get `save()`, `findById()`, `findAll()`, `deleteById()` for free!

### File to Create:
`src/main/java/com/banfico/mini_banking/repository/CustomerRepository.java`

```java
package com.banfico.mini_banking.repository;

import com.banfico.mini_banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
```

---

## Step 4: Create DTOs (Request & Response)

### Concept: Why DTOs?
- Keep internal database schema private
- Validate client input without polluting entity
- Prevent mass-assignment attacks (e.g. client trying to inject `id` or `createdAt`)

### Files to Create:
1. `src/main/java/com/banfico/mini_banking/dto/request/CustomerRequest.java`:
```java
package com.banfico.mini_banking.dto.request;

import jakarta.validation.constraints.*;

public record CustomerRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    String email,

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    String phoneNumber,

    String address
) {}
```

2. `src/main/java/com/banfico/mini_banking/dto/response/CustomerResponse.java`:
```java
package com.banfico.mini_banking.dto.response;

import java.time.LocalDateTime;

public record CustomerResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String address,
    LocalDateTime createdAt
) {}
```

---

## Step 5: Create Custom Exceptions & Global Exception Handler

### Concept: Centralized Error Handling
Use `@RestControllerAdvice` to catch exceptions globally and return clear, clean JSON responses.

### Files to Create:
1. `src/main/java/com/banfico/mini_banking/exception/ResourceNotFoundException.java`:
```java
package com.banfico.mini_banking.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

2. `src/main/java/com/banfico/mini_banking/exception/DuplicateResourceException.java`:
```java
package com.banfico.mini_banking.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
```

3. `src/main/java/com/banfico/mini_banking/exception/ErrorResponse.java`:
```java
package com.banfico.mini_banking.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}
```

4. `src/main/java/com/banfico/mini_banking/exception/GlobalExceptionHandler.java`:
```java
package com.banfico.mini_banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String messages = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            messages,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

---

## Step 6: Create Customer Service

### File to Create:
`src/main/java/com/banfico/mini_banking/service/CustomerService.java`
```java
package com.banfico.mini_banking.service;

import com.banfico.mini_banking.dto.request.CustomerRequest;
import com.banfico.mini_banking.dto.response.CustomerResponse;
import com.banfico.mini_banking.entity.Customer;
import com.banfico.mini_banking.exception.DuplicateResourceException;
import com.banfico.mini_banking.exception.ResourceNotFoundException;
import com.banfico.mini_banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }
        if (request.phoneNumber() != null && customerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.phoneNumber());
        }

        Customer customer = Customer.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .address(request.address())
            .build();

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());

        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getEmail(),
            customer.getPhoneNumber(),
            customer.getAddress(),
            customer.getCreatedAt()
        );
    }
}
```

---

## Step 7: Create Customer Controller

### File to Create:
`src/main/java/com/banfico/mini_banking/controller/CustomerController.java`

```java
package com.banfico.mini_banking.controller;

import com.banfico.mini_banking.dto.request.CustomerRequest;
import com.banfico.mini_banking.dto.response.CustomerResponse;
import com.banfico.mini_banking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Step 8: Build BankAccount, Transaction & Beneficiary

Follow the exact same workflow for the other 3 entities:

### 1. `BankAccount`
- Fields: `id`, `accountNumber` (unique), `accountType` (`SAVINGS`/`CURRENT`), `balance` (`BigDecimal`), `customer` (`@ManyToOne`)
- Endpoints:
  - `POST /api/customers/{customerId}/accounts` (Create account for customer)
  - `GET /api/customers/{customerId}/accounts` (Get customer's accounts)
  - `GET /api/accounts/{id}` (Get account by ID)

### 2. `Transaction`
- Fields: `id`, `type` (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER`), `amount` (`BigDecimal`), `description`, `account` (`@ManyToOne`), `timestamp`
- Endpoints:
  - `POST /api/accounts/{accountId}/deposit`
  - `POST /api/accounts/{accountId}/withdraw` (Validate balance before deducting!)
  - `GET /api/accounts/{accountId}/transactions`

### 3. `Beneficiary`
- Fields: `id`, `name`, `accountNumber`, `bankName`, `customer` (`@ManyToOne`)
- Endpoints:
  - `POST /api/customers/{customerId}/beneficiaries`
  - `GET /api/customers/{customerId}/beneficiaries`
  - `DELETE /api/beneficiaries/{id}`

---

## Step 9: Postman Testing Guide

Test in this exact order:

1. **Create Customer**: `POST http://localhost:8080/api/customers`
   ```json
   {
     "firstName": "Jeevan",
     "lastName": "Kumar",
     "email": "jeevan@example.com",
     "phoneNumber": "9876543210",
     "address": "Bangalore"
   }
   ```
2. **Verify 201 Created** & note down `id: 1`
3. **Get Customer**: `GET http://localhost:8080/api/customers/1`
4. **Test Bad Validation**: `POST http://localhost:8080/api/customers` with empty first name → expect **400 Bad Request**
5. **Test Duplicate Email**: Send same email again → expect **409 Conflict**
6. **Test Not Found**: `GET http://localhost:8080/api/customers/999` → expect **404 Not Found**

---

## 📝 Recommended Implementation Order

1. ✅ Create Exception package (`ResourceNotFoundException`, `DuplicateResourceException`, `ErrorResponse`, `GlobalExceptionHandler`)
2. ✅ Create `Customer` entity & run app to verify PostgreSQL table creation
3. ✅ Create `CustomerRepository`
4. ✅ Create Request/Response DTOs in `dto/request` and `dto/response`
5. ✅ Create `CustomerService`
6. ✅ Create `CustomerController`
7. ✅ Test Customer CRUD via Postman & commit to Git
8. 🔄 Repeat for `BankAccount`, `Transaction`, and `Beneficiary`

---

## ✅ Week 2 Checklist

| # | Item | Done |
|---|------|------|
| 1 | Controller-Service-Repository architecture understood | [ ] |
| 2 | Customer entity, repo, service, controller created | [ ] |
| 3 | DTOs used for request and response | [ ] |
| 4 | Hibernate created `customers` table in PostgreSQL | [ ] |
| 5 | Global Exception Handler returns clean JSON for 400, 404, 409 | [ ] |
| 6 | BankAccount entity with `@ManyToOne` relationship to Customer | [ ] |
| 7 | Transaction logic for deposit and withdraw (with balance check) | [ ] |
| 8 | Beneficiary CRUD APIs created | [ ] |
| 9 | All endpoints tested and verified in Postman | [ ] |
| 10 | Git commits made after each entity milestone | [ ] |
