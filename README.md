# EnterpriseOps Business

**Spring Boot Business Backend for EnterpriseOps AI**

EnterpriseOps Business is the Java Spring Boot backend for the EnterpriseOps AI platform. It provides REST APIs for business operations, including order retrieval, customer support-ticket management, and workflow operations that can require human approval.

The service manages business data and database operations through Spring Data JPA and Hibernate, with PostgreSQL as the persistence layer and Flyway for database schema migrations.

## Overview

EnterpriseOps AI combines AI-assisted investigation with controlled business operations.

The platform separates AI orchestration from business logic:

- **Python AI service:** Handles LLM orchestration, retrieval-augmented generation (RAG), and communication with the business backend.
- **EnterpriseOps Business:** Manages business APIs, request validation, persistence, and business workflow operations.
- **PostgreSQL:** Stores business data managed by the Java backend.

This separation keeps business data access and business operations within the Spring Boot service rather than having the Python AI service connect directly to PostgreSQL.

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java |
| Backend framework | Spring Boot |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Database | PostgreSQL |
| Database migrations | Flyway |
| API architecture | REST |
| Build tool | Maven |
| AI service integration | Python FastAPI service |

## Features

### Order Management

- Retrieve order information through REST APIs.
- Provide order data to the broader AI-powered support workflow.
- Keep order retrieval within the business backend.

### Customer Support Tickets

- Create support tickets through REST APIs.
- Retrieve ticket information.
- Persist ticket data in PostgreSQL.

### Business Workflows

The service provides a foundation for AI-assisted business workflows, including workflow creation, retrieval, and approval or rejection operations where supported by the current controller implementation.

Human approval is an important part of the design for workflows that require review before a business action is accepted.

### Persistence and Database Management

- PostgreSQL integration.
- Entity-based persistence using Spring Data JPA and Hibernate.
- Database schema migrations managed through Flyway.
- Separation of API, business logic, and database access responsibilities.

## Architecture

```text
          Client / React Dashboard
                    |
                    v
          Python AI Service
          FastAPI + LLM / RAG
                    |
             REST API calls
                    |
                    v
       EnterpriseOps Business
          Spring Boot REST APIs
                    |
                    v
           Service / Business Logic
                    |
                    v
         Spring Data JPA / Hibernate
                    |
                    v
             PostgreSQL
```

### Architecture Responsibilities

**Python AI service**
- Orchestrates AI-assisted workflows.
- Handles LLM interactions and retrieval.
- Calls the Java backend through REST APIs when business data or operations are required.

**Spring Boot business backend**
- Exposes REST endpoints for business operations.
- Validates incoming requests.
- Executes business logic and workflow operations.
- Persists and retrieves business data.

**PostgreSQL**
- Stores business records managed by the Java service.
- Uses schema migrations to manage database structure changes.

The Python AI service communicates with the Java backend through HTTP APIs and does not access the business database directly.

## Project Structure

```text
EnterpriseOps-Business/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ... Java application packages
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/
│   │           └── migration/
│   └── test/
│       └── java/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
└── README.md
```

The Java packages contain the application's controllers, business logic, persistence components, and supporting classes. The exact package organization depends on the current source code.

## API Overview

The service supports the following resource areas, subject to the current controller implementation.

| Resource | Purpose |
|---|---|
| `/api/v1/orders` | Order-related operations |
| `/api/v1/tickets` | Support-ticket creation and retrieval |
| `/api/v1/workflows` | Workflow creation, retrieval, approval, and rejection |

For exact HTTP methods, request formats, response structures, and route paths, refer to the corresponding Spring Boot controller classes and request DTOs.

## Prerequisites

Before running the service, ensure that you have:

- Java compatible with the project's Maven configuration.
- PostgreSQL installed and running.
- Git.
- A terminal or IDE capable of running Maven projects.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/vishwajeethshikari/EnterpriseOps-Business.git
cd EnterpriseOps-Business
```

### 2. Configure PostgreSQL

Create the database required by your local configuration if it does not already exist.

Configure the database URL, username, and password using your application's configuration and environment variables.

For example, in Windows PowerShell:

```powershell
$env:DB_PASSWORD = "your-local-database-password"
```

Make sure your application configuration references the correct environment variable. Set the database URL and username according to your local PostgreSQL setup.

**Security:** Never commit real database passwords, API keys, tokens, or other secrets to source control.

### 3. Run the Application

From the project root, run the Maven wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The service is configured to run on:

```text
http://localhost:8080
```

The actual port and database settings are determined by the application's configuration.

### 4. Verify the Service

Once the application has started successfully, test the available endpoints using an API client or the API documentation configured in the project.

Check the application logs to confirm that the database connection and Flyway migrations complete successfully.

## Integration with the Python AI Service

EnterpriseOps Business is designed to integrate with the Python FastAPI orchestration service.

The intended interaction is:

1. The Python service receives or processes an AI-assisted business request.
2. It retrieves relevant business information through the Java REST APIs.
3. The Java backend validates the request and performs the applicable business operation.
4. Business data is persisted in PostgreSQL.
5. Where approval is required, a workflow can be reviewed and approved or rejected through supported backend operations.

The exact integration behavior depends on the endpoints and workflow rules implemented in the current codebase.

## Security and Reliability

The backend should be configured and tested to ensure that:

- Sensitive configuration values remain outside source control.
- Incoming requests are validated.
- Business operations enforce appropriate authentication and authorization before public deployment.
- Workflow approvals and rejections are protected against unauthorized access.
- Database schema changes are managed through migrations.
- Automated tests cover key business operations and error cases.

These are security and reliability requirements; they should be considered complete only after they have been implemented and verified.

## Planned Improvements

The following areas are candidates for future development, depending on the current implementation:

- Strengthen authentication and authorization.
- Add or expand automated unit and integration tests.
- Prevent duplicate workflow actions through appropriate idempotency controls.
- Add audit logging for approvals, rejections, and important business changes.
- Improve API documentation and standardized error responses.
- Containerize the service for repeatable development and deployment.
- Strengthen integration testing between Python orchestration and Java business APIs.

## Project Goals

EnterpriseOps Business aims to provide a reliable business backend for an AI-assisted customer-support and workflow-automation platform.

The project emphasizes a clear separation between AI orchestration and business operations, structured persistence, controlled workflow execution, and a backend architecture that can evolve as new business capabilities are introduced.

## Project Status

**Status: Active development**

This repository contains the Java Spring Boot business service for the EnterpriseOps AI platform. The implemented features, API contracts, and operational behavior should be verified against the current source code before being considered production-ready.
