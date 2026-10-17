# EnterpriseOps Business — Spring Boot Backend

EnterpriseOps Business is the backend service for the EnterpriseOps AI platform. It manages business operations such as order retrieval, support-ticket management, and AI-proposed workflows requiring human approval.

The service provides REST APIs and persists business data in PostgreSQL using Spring Boot and Spring Data JPA.

## 🚀 Project Overview

This project serves as the business layer of an AI-powered customer-support and workflow automation platform.

The Python AI service handles LLM orchestration and policy retrieval, while this Java service is responsible for business data, API validation, persistence, and workflow operations.

## 🛠️ Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Persistence:** Spring Data JPA and Hibernate
- **Database:** PostgreSQL
- **Database Migrations:** Flyway
- **API Style:** REST
- **Build Tool:** Maven

## ✅ Implemented Features

- Retrieve order information through REST APIs.
- Create and retrieve customer support tickets.
- Persist business data in PostgreSQL.
- Manage database schema changes using Flyway.
- Provide an API foundation for AI-assisted business workflows.
- Support workflow creation, retrieval, and approval/rejection operations as implemented in the current codebase.

## 🏗️ Architecture

```text
Client / React Dashboard
          |
          v
Python AI Service
FastAPI + Ollama + RAG
          |
          v
EnterpriseOps Business
Spring Boot REST APIs
          |
          v
Spring Data JPA / Hibernate
          |
          v
PostgreSQL
```

The Java backend remains responsible for business data and business operations. The AI service communicates with it through REST APIs rather than connecting directly to the business database.

## 📁 Project Structure

```text
EnterpriseOps-Business/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   └── test/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
└── README.md
```

## ⚙️ Prerequisites

- Java version compatible with the project's `pom.xml`
- PostgreSQL
- Git

## ▶️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/vishwajeethshikari/EnterpriseOps-Business.git
cd EnterpriseOps-Business
```

### 2. Configure PostgreSQL

Create the required database and configure the connection using environment variables.

Example PowerShell configuration:

```powershell
$env:DB_PASSWORD = "your-local-database-password"
```

Configure the database URL, username, and password in your local Spring Boot configuration. Never commit real passwords or secrets.

### 3. Run the application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The service is configured to run at:

```text
http://localhost:8080
```

Confirm the actual database configuration and required environment variables before starting the service.

## 🔌 API Overview

The business service provides APIs for the following operations, subject to the current controller implementation:

| Resource | Purpose |
|---|---|
| `/api/v1/orders` | Order-related operations |
| `/api/v1/tickets` | Support-ticket creation and retrieval |
| `/api/v1/workflows` | Workflow creation, retrieval, approval, and rejection |

Refer to the controller classes and request DTOs for the exact paths, HTTP methods, and required request bodies.

## 🔐 Security

- Keep database passwords and credentials out of source control.
- Use environment variables for sensitive configuration.
- Validate incoming API requests.
- Add authentication and authorization before exposing business operations publicly.
- Use appropriate approval and audit mechanisms for sensitive workflow actions.

## 🗺️ Planned Improvements

- Strengthen authentication and authorization.
- Add automated unit and integration tests.
- Improve workflow idempotency and duplicate-action prevention.
- Add audit logging for workflow approvals and rejections.
- Containerize the service for easier deployment.
- Improve API documentation and error handling.

## 🎯 Project Goal

Build a reliable business backend that integrates with AI-powered investigation, retrieval, and workflow automation while keeping business data and actions under controlled backend services.

## 📌 Project Status

**Status: Active development**

This repository contains the Java Spring Boot business service for the broader EnterpriseOps AI project. Feature completeness and endpoint behavior should be verified against the current source code.
