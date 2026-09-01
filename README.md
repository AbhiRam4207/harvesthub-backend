# HarvestHub Backend

A secure REST API backend for **HarvestHub**, a smart farm-to-customer commerce platform built with Java, Spring Boot, and PostgreSQL.

The project is being developed as a practical full-stack application with a focus on **clean architecture, REST API design, JWT authentication, validation, database integration, and testability**.

> **Current status:** Backend foundation and user authentication/profile management are implemented. Additional commerce modules are under development.

---

## 🚀 Features

### ✅ Implemented

* User registration
* User login with JWT authentication
* BCrypt password hashing
* Get authenticated user's profile
* Update authenticated user's profile
* Soft account deactivation
* Role-based user model (`CUSTOMER`, `ADMIN`)
* Request validation using Jakarta Bean Validation
* Centralized exception handling
* DTO-based API contracts
* PostgreSQL database integration
* Stateless Spring Security configuration

### 🚧 Planned / In Development

* Category management
* Vegetable/product management
* Inventory management
* Cart functionality
* Order management
* Payment processing
* Admin APIs
* Email notifications
* Product image upload
* Search and filtering
* Ratings and reviews
* React frontend

---

## 🛠️ Technology Stack

| Technology              | Purpose                        |
| ----------------------- | ------------------------------ |
| Java 17                 | Programming language           |
| Spring Boot 3.3.4       | Backend framework              |
| Spring Data JPA         | Data persistence               |
| Hibernate 6.5           | ORM                            |
| PostgreSQL              | Database                       |
| Spring Security         | Authentication & authorization |
| JJWT 0.12.5             | JWT implementation             |
| BCrypt                  | Password hashing               |
| Jakarta Bean Validation | Request validation             |
| Maven                   | Build management               |
| Lombok                  | Boilerplate reduction          |
| Postman                 | API testing                    |

---

## 🏗️ Architecture

HarvestHub follows a layered backend architecture:

```text
Client / Postman
       │
       ▼
 Controller
       │
       ▼
    Service
       │
       ▼
  Repository
       │
       ▼
 PostgreSQL
```

Security and cross-cutting concerns are handled separately:

```text
                  ┌─────────────────┐
                  │  Spring Security│
                  │   JWT Filter    │
                  └────────┬────────┘
                           │
Client → Controller → Service → Repository → PostgreSQL
             │
             └── DTO / Validation / Exception Handling
```

### Architectural Principles

* Controller → Service → Repository separation
* Entities are not directly exposed through APIs
* DTOs define API contracts
* JWT-based stateless authentication
* BCrypt password hashing
* Centralized exception handling
* Validation at the API boundary
* Soft deletion using an `active` flag

---

## 🔐 Authentication & Security

HarvestHub uses **JWT-based stateless authentication**.

### Authentication Flow

```text
Login Request
     │
     ▼
AuthenticationManager
     │
     ▼
UserDetailsService
     │
     ▼
Password Verification
     │
     ▼
JWT Generated
     │
     ▼
Client
```

For protected requests:

```text
Authorization: Bearer <JWT>
            │
            ▼
       JwtAuthFilter
            │
            ▼
       Validate JWT
            │
            ▼
    SecurityContext
            │
            ▼
        Controller
```

### Security Features

* BCrypt password hashing
* JWT signature validation
* JWT expiration validation
* Stateless sessions
* Authenticated endpoint protection
* Role enum for controlled access levels
* Passwords excluded from API responses

JWT expiration is configured for **24 hours**.

---

## 📡 API Endpoints

Base URL:

```text
http://localhost:8080
```

### Authentication

| Method | Endpoint              | Authentication |
| ------ | --------------------- | -------------- |
| POST   | `/api/users/register` | Public         |
| POST   | `/api/users/login`    | Public         |

### User Profile

| Method | Endpoint             | Authentication |
| ------ | -------------------- | -------------- |
| GET    | `/api/users/profile` | JWT Required   |
| PUT    | `/api/users/profile` | JWT Required   |
| DELETE | `/api/users/profile` | JWT Required   |

### Example Registration

```http
POST /api/users/register
Content-Type: application/json
```

```json
{
  "name": "Abhiram",
  "email": "abhiram@example.com",
  "phone": "9876543210",
  "password": "password123"
}
```

### Example Login

```http
POST /api/users/login
Content-Type: application/json
```

```json
{
  "email": "abhiram@example.com",
  "password": "password123"
}
```

The login response provides a JWT token that must be supplied for protected endpoints:

```http
Authorization: Bearer <token>
```

---

## 🗄️ Database

HarvestHub uses PostgreSQL.

### Current User Table

```text
users
├── id
├── name
├── email
├── phone
├── password
├── role
├── active
├── created_at
└── updated_at
```

### Constraints

* `id` → Primary key
* `email` → Unique
* `phone` → Unique
* `role` → `CUSTOMER` or `ADMIN`
* `active` → Account status

Hibernate manages schema updates during development using:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## 📁 Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── harvesthub/
    │           └── backend/
    │               ├── config/
    │               │   └── SecurityConfig.java
    │               │
    │               ├── controller/
    │               │   └── UserController.java
    │               │
    │               ├── service/
    │               │   └── UserService.java
    │               │
    │               ├── repository/
    │               │   └── UserRepository.java
    │               │
    │               ├── entity/
    │               │   ├── User.java
    │               │   └── Role.java
    │               │
    │               ├── dto/
    │               │   ├── auth/
    │               │   └── user/
    │               │
    │               ├── security/
    │               │   ├── CustomUserDetailsService.java
    │               │   ├── JwtAuthFilter.java
    │               │   └── JwtService.java
    │               │
    │               └── exception/
    │                   ├── GlobalExceptionHandler.java
    │                   ├── ResourceNotFoundException.java
    │                   └── UnauthorizedException.java
    │
    └── resources/
        └── application.properties
```

---

## ⚙️ Configuration

Sensitive credentials are **not stored directly in the repository**.

`application.properties` uses environment variables:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION:86400000}
```

Configure the following environment variables locally:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

> Never commit database passwords, JWT secrets, API keys, or other credentials to GitHub.

---

## ▶️ Running the Project

### Prerequisites

* Java 17+
* Maven 3.8+
* PostgreSQL
* Git
* Postman

### 1. Create the database

Create a PostgreSQL database named:

```text
harvesthub
```

### 2. Configure environment variables

Set:

```text
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_strong_jwt_secret
```

### 3. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run `HarvestHubApplication` directly from IntelliJ IDEA.

The server starts at:

```text
http://localhost:8080
```

---

## 🧪 API Testing

Postman is used to test the REST APIs.

Current testing progression:

```text
1. Register User
       ↓
2. Login
       ↓
3. Copy JWT Token
       ↓
4. Get Profile
       ↓
5. Update Profile
       ↓
6. Deactivate Account
```

Protected requests require:

```http
Authorization: Bearer <JWT_TOKEN>
```

Testing includes:

* Successful requests
* Validation failures
* Authentication failures
* Duplicate email/phone scenarios
* Unauthorized access
* Profile operations
* Account deactivation

---

## 🧩 Exception Handling

A centralized `GlobalExceptionHandler` provides consistent API error handling.

Current handling includes:

| Exception           | HTTP Status |
| ------------------- | ----------: |
| Validation errors   |         400 |
| Unauthorized access |         401 |
| Resource not found  |         404 |

---

## 🌿 Git Workflow

The project is maintained using Git and GitHub.

Repository:

**HarvestHub Backend**

Current branch:

```text
main
```

Typical development workflow:

```text
Implement Feature
      ↓
Test with Postman
      ↓
git status
      ↓
git add .
      ↓
git commit -m "Add <feature>"
      ↓
git push
```

Commits are organized around meaningful feature milestones rather than one final project commit.

---

## 📚 Development Approach

HarvestHub follows a requirement-driven development approach:

```text
Requirement
     ↓
Understand
     ↓
Design Architecture
     ↓
Identify Layer Ownership
     ↓
Create Classes / Methods
     ↓
Implement Business Rules
     ↓
Validate
     ↓
Test API
     ↓
Commit
     ↓
Push to GitHub
```

The goal is to develop the ability to **design and implement features independently**, rather than simply copying implementations.

---

## 🗺️ Roadmap

### Phase 1 — Backend Foundation

* [x] Spring Boot project setup
* [x] PostgreSQL integration
* [x] User entity
* [x] User registration
* [x] JWT login
* [x] Profile management
* [x] Account deactivation
* [x] Exception handling
* [x] Validation
* [x] Git/GitHub setup

### Phase 2 — Product & Inventory

* [ ] Category management
* [ ] Vegetable/product management
* [ ] Inventory management
* [ ] Search and filtering

### Phase 3 — Shopping

* [ ] Cart
* [ ] Cart item management
* [ ] Order creation
* [ ] Order tracking

### Phase 4 — Payments & Notifications

* [ ] Payment processing
* [ ] Email notifications
* [ ] Password reset

### Phase 5 — Frontend

* [ ] React application
* [ ] Authentication UI
* [ ] Product browsing
* [ ] Cart UI
* [ ] Order UI
* [ ] Admin dashboard

### Phase 6 — Production & DevOps

* [ ] Docker
* [ ] CI/CD
* [ ] Production configuration
* [ ] Deployment

---

## 📄 Documentation

Detailed project documentation is maintained separately in:

```text
PROJECT_DOCUMENTATION.md
```

This document contains the deeper implementation details, architecture decisions, API contracts, validation rules, and development notes.

---

## 👨‍💻 Project Status

**HarvestHub Backend — Active Development**

The authentication and user-management foundation is complete. The project is being developed incrementally toward a complete farm-to-customer commerce platform with a React frontend.

---

## 📌 License

This project is currently developed as a personal learning and portfolio project.
