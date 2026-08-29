# 🚀 HireFlow - Recruitment Management System

A secure and scalable **Recruitment Management System** built using **Java and Spring Boot**. The application enables job seekers to discover vacancies and submit applications while recruiters can manage companies, vacancies, and applications using **JWT Authentication** and **Role-Based Access Control (RBAC)**.

---

## ✨ Features

- 🔐 JWT Authentication
- 👥 Role-Based Authorization (ADMIN, RECRUITER, JOB SEEKER)
- 👤 User Registration & Login
- ✉️ Email Verification
- 🔑 Secure Password Encoding
- 🏢 Company Management
- 💼 Job Vacancy Management
- 📝 Job Application Management
- 🔄 Application Status Management
- 🗄️ PostgreSQL Database
- 🔄 Flyway Database Migrations
- ✅ Input Validation
- ⚠️ Global Exception Handling
- 📖 Swagger/OpenAPI Documentation
- 🔒 Protected REST APIs

---

# 🛠 Tech Stack

| Technology | Version / Usage |
|------------|-----------------|
| Java | 21 |
| Spring Boot | 4.1 |
| Spring Security | Authentication & Authorization |
| Spring Data JPA | Data Access |
| Hibernate | ORM |
| PostgreSQL | Database |
| JJWT | JWT Authentication |
| Flyway | Database Migrations |
| SpringDoc OpenAPI | Swagger Documentation |
| Spring Mail | Email Verification |
| Maven | Build Tool |
| Lombok | Boilerplate Reduction |

---

# 📂 Project Structure

```text
HireFlow
│
├── docs/
│   └── HireFlow-Overview.png
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.kushal.hireflow
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       └── application.yml
│   │
│   └── test/
│
├── .mvn/
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md