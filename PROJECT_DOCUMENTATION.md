# HarvestHub — Project Documentation

## 1. Project Overview

HarvestHub is a smart farm-to-customer commerce platform built with Java, Spring Boot, and PostgreSQL.  
It is designed as a practical full-stack learning project with emphasis on independent feature design, layered architecture, security, and testability.

---

## 2. Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.4 |
| Build Tool | Maven |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate 6.5 |
| Security | Spring Security + JWT (JJWT 0.12.5) |
| Password Encoding | BCrypt |
| Validation | Jakarta Bean Validation |
| Utilities | Lombok |

---

## 3. Backend Architecture

**Package Structure**

```
com.harvesthub.backend
├── config
│   └── SecurityConfig
├── controller
│   └── UserController
├── service
│   └── UserService
├── repository
│   └── UserRepository
├── entity
│   ├── User
│   └── Role
├── dto
│   ├── auth
│   │   ├── RegisterRequest
│   │   ├── LoginRequest
│   │   └── LoginResponse
│   └── user
│       ├── UserResponse
│       └── UserMapper
├── security
│   ├── CustomUserDetailsService
│   ├── JwtService
│   └── JwtAuthFilter
└── exception
    ├── GlobalExceptionHandler
    ├── ResourceNotFoundException
    └── UnauthorizedException
```

**Request Flow**

```
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

Additional cross-cutting components:
- **Security Filter Chain** — stateless JWT authentication
- **Exception Handler** — centralized validation and error responses
- **Mapper** — Entity ↔ DTO conversion

---

## 4. Database Schema

### Table: users

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| phone | VARCHAR(255) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| role | VARCHAR(255) | NOT NULL, CHECK (role IN ('CUSTOMER','ADMIN')) |
| active | BOOLEAN | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

**Schema Management:** Hibernate `ddl-auto=update`  
**Dialect:** PostgreSQLDialect

---

## 5. Entities

### User
- `id`, `name`, `email`, `phone`, `password`, `role`, `active`, `createdAt`, `updatedAt`
- Lifecycle callbacks: `@PrePersist`, `@PreUpdate`

### Role (Enum)
- `CUSTOMER`
- `ADMIN`

---

## 6. Security Implementation

### Components

| Component | Responsibility |
|-----------|---------------|
| `SecurityConfig` | Configures HTTP security, CORS, session policy, public/authenticated routes |
| `PasswordEncoder` | BCrypt hashing |
| `DaoAuthenticationProvider` | Bridges `UserDetailsService` + `PasswordEncoder` |
| `CustomUserDetailsService` | Loads user by email for authentication |
| `JwtService` | Generates, parses, validates JWT tokens |
| `JwtAuthFilter` | Extracts Bearer token, validates, sets `SecurityContext` |

### JWT Configuration
- **Secret:** Configured via `app.jwt.secret`
- **Expiration:** 24 hours (`app.jwt.expiration=86400000`)
- **Algorithm:** HS256

### Public Endpoints
- `POST /api/users/register`
- `POST /api/users/login`

### Protected Endpoints
- All other endpoints require a valid JWT Bearer token

### Authentication Flow
1. Client sends `Authorization: Bearer <token>`
2. `JwtAuthFilter` extracts token
3. `JwtService` validates signature and expiration
4. `CustomUserDetailsService` loads user
5. `SecurityContext` is populated with authenticated principal
6. Controller/service proceeds with authenticated user

---

## 7. Services

### UserService
- `register(RegisterRequest)` — validates uniqueness, hashes password, creates user
- `login(LoginRequest)` — authenticates via `AuthenticationManager`, generates JWT
- `getCurrentUserProfile()` — fetches authenticated user's profile
- `updateCurrentUserProfile(RegisterRequest)` — updates name/phone, optionally password
- `deactivateCurrentUserAccount()` — soft-deactivates user account

---

## 8. DTOs (API Contracts)

| DTO | Purpose |
|-----|---------|
| `RegisterRequest` | name, email, phone, password |
| `LoginRequest` | email, password |
| `LoginResponse` | token, userId, name, email, role |
| `UserResponse` | id, name, email, phone, role, active, createdAt, updatedAt |

**Rule:** Passwords are never returned in responses.

---

## 9. Validation Rules

- **Name:** Not blank, 2–100 characters
- **Email:** Valid email format
- **Phone:** 10-digit Indian format, starts with 6–9 (`^[6-9]\d{9}$`)
- **Password:** 6–40 characters

Validation occurs at the controller layer using Jakarta validation annotations.

---

## 10. Exception Handling

### GlobalExceptionHandler
- `MethodArgumentNotValidException` → 400 Bad Request with field-level errors
- `UnauthorizedException` → 401 Unauthorized
- `ResourceNotFoundException` → 404 Not Found

---

## 11. Implemented Features

| Feature | Status | Endpoint |
|---------|--------|----------|
| User Registration | ✅ Implemented | `POST /api/users/register` |
| User Login (JWT) | ✅ Implemented | `POST /api/users/login` |
| Get Current Profile | ✅ Implemented | `GET /api/users/profile` |
| Update Profile | ✅ Implemented | `PUT /api/users/profile` |
| Deactivate Account | ✅ Implemented | `DELETE /api/users/profile` |

---

## 12. How to Run

### Prerequisites
1. Java 17+
2. Maven 3.8+
3. PostgreSQL running on localhost:5432
4. Database `harvesthub` created

### Steps
```bash
# 1. Ensure PostgreSQL is running and database exists
psql -U postgres -c "CREATE DATABASE harvesthub;"

# 2. Update credentials in application.properties if needed
# spring.datasource.username=postgres
# spring.datasource.password=postgres

# 3. Run the application
mvn spring-boot:run
```

Server starts on `http://localhost:8080`

---

## 13. Testing Guide

### 1. Register a new user
```http
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "name": "Abhiram",
  "email": "abhiram@example.com",
  "phone": "9876543210",
  "password": "password123"
}
```

### 2. Login
```http
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "abhiram@example.com",
  "password": "password123"
}
```
Copy `token` from response.

### 3. Get Profile (authenticated)
```http
GET http://localhost:8080/api/users/profile
Authorization: Bearer <token>
```

### 4. Update Profile
```http
PUT http://localhost:8080/api/users/profile
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Abhiram Updated",
  "phone": "9876543210",
  "password": "newpassword123"
}
```

### 5. Deactivate Account
```http
DELETE http://localhost:8080/api/users/profile
Authorization: Bearer <token>
```

---

## 14. Key Design Decisions

1. **Stateless JWT** — No server-side session storage; token carries identity
2. **Layered Architecture** — Controller → Service → Repository separation
3. **DTOs for API Contracts** — Entities never exposed directly
4. **Enum for Role** — Controlled vocabulary, DB-level check constraint
5. **Soft Delete** — `active` flag instead of hard delete
6. **Centralized Exception Handling** — Consistent error response format
7. **Password Encoding** — BCrypt, never store plain text
8. **Validation at API Boundary** — Bean validation on request DTOs

---

## 15. Future Modules (Planned)

- Product / Inventory management
- Cart functionality
- Order management
- Payment integration
- Admin dashboard APIs
- Email notifications
- Password reset / forgot password
- Image upload for products
- Search and filtering
- Ratings and reviews

---

## 16. Learning Notes

This project follows the principle:  
**Requirement → Understanding → Architecture → Layer Ownership → Classes/Methods → Validation → Business Rules → Testing**

The objective is independent development capability, not code copying.
