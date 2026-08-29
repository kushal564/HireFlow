# HireFlow — Recruitment Management System

HireFlow is a backend-focused Recruitment Management System built with Java and Spring Boot. It provides a secure REST API for managing users, companies, job vacancies, and job applications.

The application implements JWT-based authentication, role-based authorization, email verification, refresh-token management, database migrations with Flyway, validation, centralized exception handling, and Docker-based deployment.

The project is deployed using Docker on Render with a managed PostgreSQL database.

---

## 🚀 Live Deployment

**Backend:**  
https://hireflow-3rcl.onrender.com

**GitHub Repository:**  
https://github.com/kushal564/HireFlow

> Note: The application is deployed on Render's free infrastructure, so the service may take some time to respond after a period of inactivity.

---

# 📌 Project Overview

HireFlow provides a complete backend workflow for a recruitment platform.

The system supports two primary roles:

- **RECRUITER**
- **CANDIDATE**

### Recruiter capabilities

Recruiters can:

- Create companies
- Update their own companies
- Delete their own companies
- Create job vacancies
- Update their vacancies
- Manage recruitment applications
- View applications associated with their vacancies
- Update application statuses

### Candidate capabilities

Candidates can:

- Register an account
- Verify their email address
- Login securely
- Browse companies and vacancies
- Apply for vacancies
- View their own applications
- Track application status

---

# ✨ Features

## Authentication & Security

- User registration
- Email verification
- Login authentication
- JWT access tokens
- JWT refresh tokens
- Refresh-token persistence
- Refresh-token cleanup
- HTTP-only refresh-token cookie
- Role-based authorization
- Spring Security filter chain
- Custom JWT authentication filter
- Protected REST endpoints
- Centralized security exception handling

## User Management

- User registration
- User login
- User role management
- Candidate and recruiter roles
- Authenticated user retrieval
- Role-based access control

## Company Management

- Create company
- Get all companies
- Get company by ID
- Update company
- Delete company
- Recruiter ownership validation

## Vacancy Management

- Create vacancy
- Update vacancy
- Retrieve vacancies
- Filter vacancies
- Recruiter-specific vacancy management
- Company-vacancy relationship

## Application Management

- Candidate applies for vacancy
- Candidate views own applications
- Recruiter views applications
- Recruiter updates application status
- Application ownership and authorization checks

## Database

- PostgreSQL
- Spring Data JPA
- Hibernate
- Flyway database migrations
- Versioned schema management

## Email

- Spring Mail
- SMTP integration
- Brevo SMTP relay
- Email verification
- Configurable verification-token expiration

## API Documentation

- OpenAPI
- Swagger UI

## Deployment

- Docker
- Docker Compose for local development
- Render Web Service
- Render PostgreSQL
- Production environment variables
- HTTPS deployment

---

# 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1 | Backend framework |
| Spring Web | REST API development |
| Spring Security | Authentication & authorization |
| JWT | Access & refresh token authentication |
| Spring Data JPA | Data access layer |
| Hibernate | ORM |
| PostgreSQL | Production database |
| Flyway | Database migrations |
| Spring Mail | Email functionality |
| Brevo SMTP | Email delivery |
| Maven | Build & dependency management |
| Lombok | Boilerplate reduction |
| Bean Validation | Request validation |
| OpenAPI / Swagger | API documentation |
| Docker | Containerization |
| Docker Compose | Local multi-container setup |
| Render | Cloud deployment |
| Git & GitHub | Version control |

---
# 🏗️ System Architecture

The following diagram provides a complete overview of HireFlow's backend architecture, including the application layers, authentication and authorization flow, database design, core modules, technology stack, and production deployment architecture.

<p align="center">
  <img src="docs/HireFlow-Complete%20picture.png" alt="HireFlow Complete Architecture" width="100%">
</p>

### Architecture Overview

HireFlow follows a layered architecture with clear separation of responsibilities:

```text
Client
   │
   ▼
Spring Security Filter Chain
   │
   ▼
REST Controllers
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
PostgreSQL

# 🏗️ Architecture

HireFlow follows a layered backend architecture.

```text
Client
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
PostgreSQL Database

Security is applied before the request reaches the controller:

Client
  │
  │ Authorization: Bearer <JWT>
  ▼
Spring Security Filter Chain
  │
  ▼
JwtAuthenticationFilter
  │
  ├── Extract JWT
  ├── Validate JWT
  ├── Extract username
  ├── Load UserDetails
  └── Set Authentication
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

  🔐 Authentication Flow

HireFlow uses JWT-based authentication.

Registration
Client
   │
   ▼
POST /api/auth/register
   │
   ▼
AuthController
   │
   ▼
AuthService
   │
   ├── Validate request
   ├── Create user
   ├── Assign role
   ├── Save user
   └── Generate email verification token
   │
   ▼
EmailVerificationService
   │
   ▼
SMTP / Brevo
   │
   ▼
Verification Email

The user must verify the email before completing the normal authentication flow.

  🔑 Login Flow

Client
   │
   ▼
POST /api/auth/login
   │
   ▼
Spring Security
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

After successful authentication:

Authenticated User
       │
       ▼
JwtService
       │
       ├── Access Token
       │
       └── Refresh Token

The access token is used to access protected APIs.

🎟️ JWT Access Token

The access token is sent in the HTTP Authorization header:

Authorization: Bearer <access-token>

The JwtAuthenticationFilter:

Reads the Authorization header.
Checks for the Bearer prefix.
Extracts the JWT.
Validates the token.
Extracts the username/subject.
Loads the user.
Creates an authenticated SecurityContext.
Allows the request to continue through the filter chain.

🔄 Refresh Token

Refresh tokens are persisted in the database.

They are used to obtain a new access token when the access token expires.

The project also includes refresh-token cleanup functionality to remove or manage expired/old refresh tokens.

The refresh token is handled through a secure HTTP cookie configuration in production.

👥 Role-Based Authorization

HireFlow uses roles stored in the database.

Current roles:

RECRUITER
CANDIDATE

Authorization is enforced using Spring Security and application-level ownership checks.

For example:

A recruiter can update only their own company.

Recruiter A
   │
   ├── Company A → Allowed
   │
   └── Company B → Forbidden

This prevents users from modifying resources that belong to another recruiter.

🏢 Company Management

Companies are associated with recruiters.

Main endpoints
POST   /api/companies
GET    /api/companies
GET    /api/companies/{id}
PUT    /api/companies/{id}
DELETE /api/companies/{id}
Company creation

Only authenticated recruiters can create companies.

The created company is associated with the currently authenticated recruiter.

Company update/delete

A recruiter can modify or delete only their own company.

💼 Vacancy Management

Vacancies are associated with companies and managed through the recruiter workflow.

Example vacancy:

{
  "title": "Java Backend Developer",
  "description": "We are looking for a Java backend developer with experience in Spring Boot, REST APIs, and PostgreSQL.",
  "location": "Noida",
  "salaryFrom": 600000,
  "salaryTo": 1000000,
  "companyId": 1
}

Vacancy functionality includes:

Creating vacancies
Updating vacancies
Retrieving vacancies
Filtering vacancies
Recruiter ownership validation

📄 Application Management

Candidates can apply to available vacancies.

Example:

POST /api/applications

Request:

{
  "vacancyId": 1
}

The application associates:

Candidate
    │
    ▼
Application
    │
    ▼
Vacancy
    │
    ▼
Company

Candidates can view their applications, while recruiters can view applications related to their recruitment process.

📊 Application Status

Recruiters can update application status.

Example:

PATCH /api/applications/{id}/status

Example request:

{
  "status": "ACCEPTED"
}

The application status represents the current stage of the recruitment process.

🗄️ Database Design

HireFlow uses PostgreSQL with JPA/Hibernate.

Main entities include:

User
Role
Company
Vacancy
Application
RefreshToken
EmailVerificationToken

Relationships include:

Role
 │
 └── User

User
 │
 └── Company
       │
       └── Vacancy
              │
              └── Application
                    │
                    └── User

Authentication-related entities:

User
 │
 ├── RefreshToken
 │
 └── EmailVerificationToken
 
 
🧬 Database Migrations

Flyway is used to version and manage database schema changes.

Current migrations include:

V1  - Create roles table
V2  - Create users table
V3  - Insert default roles
V4  - Create companies table
V5  - Create vacancies table
V6  - Create applications table
V7  - Add email_verified to users
V8  - Create email verification tokens table
V9  - Seed demo data
V10 - Create refresh tokens table
V11 - Add last_used_at to refresh tokens

Production uses:

spring:
  jpa:
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true

This means Hibernate validates the existing schema instead of creating or modifying database tables automatically.

Flyway is responsible for applying the database migrations.

🧯 Exception Handling

The project uses centralized exception handling.

Custom exceptions include:

BadRequestException
ForbiddenException
ResourceNotFoundException

A global exception handler provides consistent API error responses.

Example structure:

{
  "status": 403,
  "error": "Forbidden",
  "message": "You can update only your own company",
  "path": "/api/companies/1"
}

This keeps error handling consistent across controllers.

✅ Validation

Request DTOs use Jakarta Bean Validation.

Examples include:

@NotBlank
@NotNull
@Valid

Validation is performed before the request reaches the service layer.

Invalid requests are handled through the centralized exception handler.

📦 Project Structure
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── kushal/
│   │           └── hireflow/
│   │               │
│   │               ├── application/
│   │               │   ├── controller/
│   │               │   ├── dto/
│   │               │   ├── entity/
│   │               │   ├── repository/
│   │               │   └── service/
│   │               │
│   │               ├── auth/
│   │               │   ├── controller/
│   │               │   ├── dto/
│   │               │   ├── entity/
│   │               │   ├── repository/
│   │               │   ├── security/
│   │               │   └── service/
│   │               │
│   │               ├── company/
│   │               │   ├── controller/
│   │               │   ├── dto/
│   │               │   ├── entity/
│   │               │   ├── repository/
│   │               │   └── service/
│   │               │
│   │               ├── common/
│   │               │   ├── config/
│   │               │   ├── controller/
│   │               │   ├── exception/
│   │               │   └── response/
│   │               │
│   │               ├── enums/
│   │               │
│   │               ├── user/
│   │               │   ├── controller/
│   │               │   ├── dto/
│   │               │   ├── entity/
│   │               │   ├── repository/
│   │               │   └── service/
│   │               │
│   │               └── vacancy/
│   │                   ├── controller/
│   │                   ├── dto/
│   │                   ├── entity/
│   │                   ├── repository/
│   │                   ├── service/
│   │                   └── specification/
│   │
│   └── resources/
│       ├── db/
│       │   └── migration/
│       │       ├── V1__create_roles_table.sql
│       │       ├── V2__create_users_table.sql
│       │       ├── V3__insert_default_roles.sql
│       │       ├── V4__create_companies_table.sql
│       │       ├── V5__create_vacancies_table.sql
│       │       ├── V6__create_applications_table.sql
│       │       ├── V7__add_email_verified_to_users.sql
│       │       ├── V8__create_email_verification_tokens_table.sql
│       │       ├── V9__seed_demo_data.sql
│       │       ├── V10__create_refresh_tokens_table.sql
│       │       └── V11__add_last_used_at_to_refresh_tokens.sql
│       │
│       └── application.yml
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
🐳 Docker

HireFlow can be run using Docker and Docker Compose.

The project contains:

Dockerfile
docker-compose.yml
.dockerignore

The Docker setup allows the Spring Boot application and PostgreSQL database to run as separate containers.

Example architecture:

Docker Compose
      │
      ├── hireflow-app
      │       │
      │       └── Spring Boot
      │
      └── PostgreSQL
▶️ Running Locally
Prerequisites

Install:

Java 21
Maven
Docker Desktop
Git
Clone the repository
git clone https://github.com/kushal564/HireFlow.git

Navigate into the project:

cd HireFlow
🔐 Environment Variables

Sensitive configuration is not committed to GitHub.

Create a local .env file containing the required environment variables.

Example:

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

Never commit .env or production secrets to GitHub.

🐳 Run with Docker Compose

Build and start the containers:

docker compose up --build

Run in detached mode:

docker compose up -d --build

Check running containers:

docker compose ps

View application logs:

docker logs hireflow-app

Stop the containers:

docker compose down
🧪 Testing

The project contains Spring Boot tests under:

src/test/

Tests can be executed with Maven:

mvn test

For a package build:

mvn clean package
📚 API Documentation

Swagger/OpenAPI is configured for API documentation.

When running locally, Swagger UI can be accessed at:

http://localhost:8080/swagger-ui/index.html

The deployed application can also be accessed through its Render URL.

🔗 Main API Endpoints
Authentication
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/resend-verification
GET  /api/auth/verify-email
Companies
POST   /api/companies
GET    /api/companies
GET    /api/companies/{id}
PUT    /api/companies/{id}
DELETE /api/companies/{id}
Vacancies
POST   /api/vacancies
GET    /api/vacancies
GET    /api/vacancies/{id}
PUT    /api/vacancies/{id}
Applications
POST  /api/applications
GET   /api/applications/my
GET   /api/applications/recruiter
PATCH /api/applications/{id}/status

Refer to the Swagger/OpenAPI documentation for the complete request and response specifications.

☁️ Production Deployment

The application is deployed using:

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

The production database is managed separately through Render PostgreSQL.

The application uses environment variables for:

Database credentials
JWT secrets
SMTP credentials
Email configuration
Cookie configuration
Production URLs
🔄 Production Database Initialization

The production database starts without the application's schema.

When the application starts:

Spring Boot
     │
     ▼
Flyway
     │
     ├── V1
     ├── V2
     ├── V3
     ├── ...
     └── V11
     │
     ▼
PostgreSQL schema ready
     │
     ▼
Hibernate validates schema
     │
     ▼
Application starts

This allows the production database schema to be reproducibly created from version-controlled migration scripts.

🔒 Production Security Considerations

Production configuration uses environment variables instead of hard-coded secrets.

Sensitive values include:

DB_PASSWORD
JWT_ACCESS_SECRET
JWT_REFRESH_SECRET
MAIL_PASSWORD

These values should never be committed to source control.

The production refresh cookie uses:

JWT_REFRESH_COOKIE_SECURE=true

because the deployed application is served through HTTPS.

📈 Future Improvements

Possible future enhancements include:

Advanced recruiter dashboard
Candidate profile management
Resume upload and storage
Job search improvements
Pagination across more endpoints
Advanced vacancy filtering
Application notifications
Automated email notifications
Password reset functionality
Account management
Audit logging
Application analytics
CI/CD pipeline
Automated deployment
Cloud object storage for resumes
Improved automated test coverage
Monitoring and observability
🎯 Learning Objectives

This project was built to demonstrate practical backend development concepts including:

Java
Spring Boot
REST API development
Dependency Injection
Layered architecture
Spring Security
JWT authentication
Role-based authorization
JPA/Hibernate
PostgreSQL
Flyway migrations
DTO-based API design
Validation
Exception handling
Email verification
Refresh-token management
Docker
Docker Compose
Cloud deployment
Environment-based configuration


👨‍💻 Author

Kushal Yadav

GitHub:
https://github.com/kushal564

⭐ Project Status

Production deployed and functional.

The deployed application has been tested across the major recruitment workflow:

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
Candidate Applies
      ↓
Recruiter Views Application
      ↓
Recruiter Updates Status
      ↓
Candidate Views Updated Status

📄 License

This project is intended primarily as a portfolio and learning project.


### One thing before you paste it

I intentionally **didn't put any real passwords, JWT secrets, Brevo credentials, or database credentials** into the README.

Also, I would **not change your code just for the README**. This version describes the project you have actually deployed.

Once you've pasted it into `README.md`, **don't push immediately**. Show me the README preview first (or tell me you've pasted it), and we can make sure the GitHub presentation looks professional before the final commit.