# HireFlow — Recruitment Management System

HireFlow is a **backend-focused Recruitment Management System** built with **Java 21 and Spring Boot**. It provides a secure REST API for managing users, companies, job vacancies, and job applications.

The application implements **JWT authentication, role-based authorization, email verification, refresh-token management, PostgreSQL persistence, Flyway migrations, request validation, centralized exception handling, unit testing, and Docker-based deployment**.

The application is deployed on **Render** with a managed PostgreSQL database.

---

## 🌐 Live Deployment

**Live Backend:**  
https://hireflow-3rcl.onrender.com

**GitHub Repository:**  
https://github.com/kushal564/HireFlow

> **Note:** The application is deployed on Render's free infrastructure, so the service may take some time to respond after a period of inactivity.

---

## ✨ Features

### 🔐 Authentication & Security

- User registration and login
- Email verification
- JWT access tokens
- JWT refresh tokens
- Persistent refresh-token management
- Refresh-token cleanup
- HTTP-only refresh-token cookie
- Spring Security filter chain
- Custom JWT authentication filter
- Stateless authentication
- Role-based authorization
- BCrypt password encoding
- Centralized security exception handling

### 👥 Roles & Authorization

The application supports three roles:

- **ADMIN**
- **RECRUITER**
- **CANDIDATE**

#### ADMIN

- Update user roles

#### RECRUITER

- Create, update, and delete owned companies
- Create, update, and delete owned vacancies
- View recruitment-related applications
- Update application status

#### CANDIDATE

- Register and verify account
- Browse companies and vacancies
- Apply for vacancies
- View own applications
- Track application status

Authorization is enforced using **Spring Security**, with additional **service-layer ownership checks** for recruiter-managed resources.

### 🏢 Company Management

- Create company
- Retrieve companies
- Retrieve company by ID
- Update company
- Delete company
- Recruiter ownership validation

### 💼 Vacancy Management

- Create vacancy
- Retrieve vacancies
- Retrieve vacancy by ID
- Update vacancy
- Delete vacancy
- Filtering
- Pagination
- Sorting
- Salary-range validation
- Recruiter ownership validation

### 📝 Application Management

- Candidate applies for vacancy
- Candidate views own applications
- Recruiter views applications associated with their vacancies
- Recruiter updates application status

### ✉️ Email

- Email verification
- SMTP integration
- Brevo SMTP relay
- Configurable verification-token expiration

---

## 🏗️ Architecture

The application follows a **layered backend architecture with domain-oriented modules**.

<p align="center">
  <img src="docs/HireFlow-Complete%20picture.png" alt="HireFlow Architecture" width="100%">
</p>

### Request Flow

```text
Client
   │
   ▼
Spring Security Filter Chain
   │
   ▼
REST Controller
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
PostgreSQL
```

### Protected Request Flow

```text
Client
   │
   │ Authorization: Bearer <JWT>
   ▼
JwtAuthenticationFilter
   │
   ├── Extract JWT
   ├── Validate signature & expiration
   ├── Extract user information
   ├── Load authenticated user
   └── Set SecurityContext
   │
   ▼
Authorization
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

### Project Structure

```text
com.kushal.hireflow
│
├── auth
├── company
├── vacancy
├── application
├── user
├── enums
└── common
```

The major domains separate **controllers, DTOs, entities, repositories, and services** according to responsibility.

---

## 🔑 Authentication Flow

HireFlow uses separate **access and refresh JWTs**.

### Login

```text
Client
  │
  ▼
/api/auth/login
  │
  ▼
AuthenticationManager
  │
  ▼
DaoAuthenticationProvider
  │
  ▼
CustomUserDetailsService
  │
  ▼
UserRepository
  │
  ▼
PostgreSQL
```

After successful authentication, `JwtService` generates:

- **Access Token**
- **Refresh Token**

### Access Token

The access token is sent using:

```text
Authorization: Bearer <access-token>
```

The `JwtAuthenticationFilter` validates the token and establishes authentication in the Spring Security context.

### Refresh Token

Refresh tokens are persisted in PostgreSQL and include:

- Unique token identifier
- Expiration handling
- Last-used tracking
- Cleanup of expired tokens
- HTTP-only cookie handling
- Secure production cookie configuration

---

## 🛡️ Role-Based Authorization

Roles are stored in the database:

```text
ADMIN
RECRUITER
CANDIDATE
```

The application combines **endpoint-level authorization** with **service-layer ownership validation**.

For example:

```text
Recruiter A
   │
   ├── Company A → Allowed
   │
   └── Company B → Forbidden
```

This prevents recruiters from modifying resources belonging to other recruiters.

The ADMIN role currently has access to user role management:

```text
PATCH /api/users/{id}/role
```

---

## 🔄 Recruitment Workflow

```text
Recruiter
    │
    ▼
Create Company
    │
    ▼
Create Vacancy
    │
    ▼
Candidate
    │
    ▼
Apply for Vacancy
    │
    ▼
Recruiter
    │
    ▼
View Application
    │
    ▼
Update Application Status
    │
    ▼
Candidate
    │
    ▼
View Updated Status
```

---

## 🗄️ Database

HireFlow uses **PostgreSQL**, **Spring Data JPA**, **Hibernate**, and **Flyway**.

### Main Entities

- `User`
- `Role`
- `Company`
- `Vacancy`
- `Application`
- `RefreshToken`
- `EmailVerificationToken`

### Core Relationships

```text
Role
 │
 └── User
      │
      └── Company
           │
           └── Vacancy
                │
                └── Application
                     │
                     └── User
```

### Flyway Migrations

The project contains **11 versioned Flyway migrations** covering:

| Version | Purpose |
|---|---|
| V1 | Roles |
| V2 | Users |
| V3 | Default roles |
| V4 | Companies |
| V5 | Vacancies |
| V6 | Applications |
| V7 | Email verification state |
| V8 | Email verification tokens |
| V9 | Demo data |
| V10 | Refresh tokens |
| V11 | Refresh-token last-used tracking |

Production uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true
```

Hibernate **validates the production schema**, while **Flyway manages schema changes through versioned migrations**.

---

## 🧯 Exception Handling & Validation

The application uses centralized exception handling through a global exception handler.

Custom exceptions include:

- `BadRequestException`
- `ForbiddenException`
- `ResourceNotFoundException`

Request DTOs use Jakarta Bean Validation annotations such as:

- `@NotBlank`
- `@NotNull`
- `@Positive`
- `@Valid`

Invalid requests are handled consistently through the application's exception-handling layer.

---

## 🧪 Testing

The project includes automated **unit tests using JUnit 5 and Mockito**.

The current test suite contains **23 passing unit tests** covering JWT functionality and important service-layer business rules.

| Test Class | Tests | Focus |
|---|---:|---|
| `JwtServiceTest` | 8 | JWT generation, extraction, validation, and invalid-token handling |
| `CompanyServiceTest` | 7 | Company authorization, ownership, and service behavior |
| `VacancyServiceTest` | 8 | Vacancy authorization, ownership, and filter validation |
| **Total** | **23** | |

### Testing Focus

The tests primarily cover:

- JWT authentication logic
- Token validation
- Authorization boundaries
- Recruiter ownership rules
- Unauthorized resource modification
- Resource-not-found handling
- Business-rule validation
- Repository interaction behavior

The current suite is **unit-level testing**. Controller-level and broader integration testing are not presented as completed functionality.

### Run Tests

```bash
mvn test
```

Run a complete Maven build:

```bash
mvn clean package
```

---

## 📖 API Documentation

Swagger/OpenAPI is configured for interactive API documentation and API testing.

### Local Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger provides the available REST endpoints together with their request and response schemas.

---

## 🔗 Main API Endpoints

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/resend-verification
GET  /api/auth/verify-email
```

### User / Admin

```text
PATCH /api/users/{id}/role
```

Restricted to `ADMIN`.

### Companies

```text
POST   /api/companies
GET    /api/companies
GET    /api/companies/{id}
PUT    /api/companies/{id}
DELETE /api/companies/{id}
```

### Vacancies

```text
POST   /api/vacancies
GET    /api/vacancies
GET    /api/vacancies/{id}
PUT    /api/vacancies/{id}
DELETE /api/vacancies/{id}
```

### Applications

```text
POST  /api/applications
GET   /api/applications/my
GET   /api/applications/recruiter
PATCH /api/applications/{id}/status
```

See Swagger/OpenAPI for complete request and response specifications.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Java 21** | Programming language |
| **Spring Boot 4.1** | Backend framework |
| **Spring Web** | REST APIs |
| **Spring Security** | Authentication & authorization |
| **JJWT** | JWT implementation |
| **Spring Data JPA** | Data access |
| **Hibernate** | ORM |
| **PostgreSQL** | Relational database |
| **Flyway** | Database migrations |
| **Spring Mail** | Email functionality |
| **Brevo SMTP** | Email delivery |
| **Jakarta Bean Validation** | Request validation |
| **JUnit 5** | Automated testing |
| **Mockito** | Unit-test mocking |
| **OpenAPI / Swagger** | API documentation |
| **Maven** | Build & dependency management |
| **Lombok** | Boilerplate reduction |
| **Docker** | Containerization |
| **Docker Compose** | Local container orchestration |
| **Render** | Cloud deployment |
| **Git & GitHub** | Version control |

---

## 🐳 Docker

The project includes:

- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`

Docker Compose can run the Spring Boot application together with PostgreSQL.

```text
Docker Compose
      │
      ├── Spring Boot Application
      │
      └── PostgreSQL
```

### Run with Docker Compose

```bash
docker compose up --build
```

Run in detached mode:

```bash
docker compose up -d --build
```

Stop:

```bash
docker compose down
```

---

## ▶️ Local Setup

### Prerequisites

- Java 21
- Maven
- Docker Desktop
- Git

### Clone

```bash
git clone https://github.com/kushal564/HireFlow.git
cd HireFlow
```

### Environment Variables

Sensitive configuration is kept outside source control.

Example:

```env
DB_USERNAME=hireflow
DB_PASSWORD=your_database_password

JWT_ACCESS_SECRET=your_access_secret
JWT_REFRESH_SECRET=your_refresh_secret

MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your_brevo_username
MAIL_PASSWORD=your_brevo_password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
MAIL_FROM=your_verified_email

JWT_REFRESH_COOKIE_SECURE=false
JWT_REFRESH_COOKIE_SAME_SITE=Strict
JWT_REFRESH_COOKIE_PATH=/api/auth

EMAIL_VERIFICATION_BASE_URL=http://localhost:8080/api/auth/verify-email
EMAIL_VERIFICATION_EXPIRATION_HOURS=24

JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
```

> **Never commit `.env` files or production secrets to source control.**

---

## ☁️ Production Deployment

The production setup uses **GitHub, Render, Docker, and managed PostgreSQL**.

```text
GitHub
   │
   ▼
Render Web Service
   │
   ▼
Dockerfile
   │
   ▼
Spring Boot Application
   │
   ▼
Render PostgreSQL
```

Production configuration is supplied through environment variables for:

- Database credentials
- JWT secrets
- SMTP credentials
- Email configuration
- Cookie configuration
- Production URLs

---

## 🔒 Production Security

Production secrets are provided through environment variables rather than being hard-coded.

Sensitive values include:

- `DB_PASSWORD`
- `JWT_ACCESS_SECRET`
- `JWT_REFRESH_SECRET`
- `MAIL_PASSWORD`

The production refresh cookie uses:

```text
JWT_REFRESH_COOKIE_SECURE=true
```

because the deployed application is served over HTTPS.

---

## 📈 Future Improvements

Potential future enhancements include:

- Advanced recruiter dashboard
- Candidate profile management
- Resume upload and storage
- Advanced job search
- Application notifications
- Password reset
- Account management
- Audit logging
- Application analytics
- GitHub Actions CI pipeline
- Automated deployment workflow
- Cloud object storage for resumes
- Controller-level and integration test coverage
- Monitoring and observability

These are potential extensions and are **not presented as currently implemented features**.

---

## 🎯 Project Highlights

HireFlow demonstrates practical backend development using:

- **Java 21 + Spring Boot**
- **REST API architecture**
- **Spring Security + JWT**
- **Access & refresh-token authentication**
- **HTTP-only refresh-token cookies**
- **Role-based authorization**
- **Resource ownership validation**
- **PostgreSQL + JPA/Hibernate**
- **Flyway database migrations**
- **Email verification**
- **Bean Validation**
- **Global exception handling**
- **JUnit 5 + Mockito**
- **23 passing unit tests**
- **Docker + Docker Compose**
- **Render cloud deployment**
- **OpenAPI / Swagger**

---

## 👨‍💻 Author

**Kushal Yadav**

GitHub:  
https://github.com/kushal564/HireFlow

---

## ⭐ Project Status

**Production deployed and functional.**

The major recruitment workflow has been tested through the deployed API:

```text
Recruiter Login
      ↓
JWT Authentication
      ↓
Company Creation
      ↓
Vacancy Creation
      ↓
Candidate Login
      ↓
Candidate Application
      ↓
Recruiter Views Application
      ↓
Recruiter Updates Status
      ↓
Candidate Views Updated Status
```

The project currently includes:

- Production deployment on Render
- Managed PostgreSQL database
- JWT access and refresh-token authentication
- ADMIN, RECRUITER, and CANDIDATE roles
- Recruiter ownership validation
- 11 Flyway migrations
- Docker-based deployment
- Swagger/OpenAPI documentation
- 23 automated unit tests using JUnit 5 and Mockito

---

## 📄 License

This project is intended primarily as a portfolio and learning project.