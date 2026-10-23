# EnterpriseOps Business

**Spring Boot Business Backend for EnterpriseOps AI**

EnterpriseOps Business is the Java Spring Boot backend for an AI-assisted customer support and business workflow platform. It provides REST APIs for order retrieval, support-ticket management, user authentication, and workflows that can require human approval.

The service manages business operations and persistence using Spring Data JPA, Hibernate, PostgreSQL, and Flyway database migrations. It integrates with a Python FastAPI service that orchestrates AI agents and retrieval-augmented generation (RAG).

## Overview

EnterpriseOps AI separates AI reasoning from business execution:

- **Python AI service:** Orchestrates LLM-powered investigation, policy retrieval, and support triage.
- **EnterpriseOps Business:** Manages business APIs, authentication, authorization, workflow operations, and database access.
- **PostgreSQL:** Stores orders, support tickets, workflows, and user accounts.

The AI service communicates with the Java backend through REST APIs rather than connecting directly to PostgreSQL.

## Features

### Order Management
- Retrieve order information through REST APIs.
- Provide order data to the AI-assisted investigation workflow.
- Keep business data access within the Java backend.

### Support Ticket Management
- Retrieve support-ticket information.
- Persist ticket records in PostgreSQL.
- Support controlled ticket creation through the workflow approval process, where enforced by the current implementation.

### Business Workflow Management
- Create and retrieve business workflows.
- Support workflow approval and rejection operations.
- Separate AI-generated proposals from business execution.
- Require authorized approval before ticket creation in the implemented approval flow.

### Authentication and User Management
- Register users with a username, email address, and password.
- Authenticate using a username or email address.
- Hash passwords using Spring Security's BCrypt password encoder.
- Issue JWT bearer access tokens after successful login.
- Include user identity and role claims in JWTs.
- Assign the `USER` role during public registration.
- Support initial administrator provisioning through environment-based configuration.

### Authorization
- Validate JWT bearer tokens using Spring Security's OAuth2 Resource Server support.
- Configure role-based restrictions for workflow approval and rejection.
- Keep authentication and authorization enforcement within the Java backend.

**Security note:** These capabilities should be considered fully verified only after successful runtime tests confirm token validation, role restrictions, and protection of all sensitive endpoints.

### Database and Persistence
- PostgreSQL integration.
- Spring Data JPA and Hibernate.
- Flyway-managed schema migrations.
- Entity, repository, service, and controller separation.

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 21 |
| Backend framework | Spring Boot 4.1.1 |
| API architecture | REST |
| Security | Spring Security |
| Authentication | JWT bearer tokens |
| Password hashing | BCrypt |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Database | PostgreSQL |
| Schema migrations | Flyway |
| Build tool | Maven |
| API documentation | Springdoc OpenAPI |
| AI integration | Python FastAPI service |

## Architecture

```text
                Client / Dashboard
                       |
                       v
              Python AI Service
               FastAPI + LLM/RAG
                       |
                  REST APIs
                       |
                       v
             EnterpriseOps Business
                Spring Boot
                       |
          +------------+-------------+
          |            |             |
          v            v             v
       Security     Services     Controllers
       JWT/Roles    Workflows     REST APIs
          |            |
          +------------+
                       |
                       v
             Spring Data JPA
                 Hibernate
                       |
                       v
                 PostgreSQL
```

### Architecture Responsibilities

**Python AI service**
- Coordinates AI agents.
- Retrieves relevant policy information.
- Produces investigation and triage proposals.
- Calls the Java backend through HTTP APIs.

**Spring Boot business backend**
- Validates incoming requests.
- Authenticates users and validates JWTs.
- Enforces configured authorization rules.
- Executes business and workflow operations.
- Manages persistence.

**PostgreSQL**
- Stores business records.
- Uses Flyway migrations to manage schema changes.

## Project Structure

```text
EnterpriseOps-Business/
├── src/
│   ├── main/
│   │   ├── java/com/driveease/gateway/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   └── test/java/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## API Overview

The following paths describe the intended API areas. Confirm exact HTTP methods and request/response formats using the current controller classes or generated API documentation.

| Endpoint | Purpose | Access |
|---|---|---|
| `POST /api/v1/auth/register` | Register a user | Public |
| `POST /api/v1/auth/login` | Authenticate and obtain a JWT | Public |
| `/api/v1/orders` | Order operations | Authentication required unless explicitly configured otherwise |
| `/api/v1/tickets` | Support-ticket operations | Authentication required unless explicitly configured otherwise |
| `/api/v1/workflows` | Workflow operations | Authentication required unless explicitly configured otherwise |
| `POST /api/v1/workflows/{workflowNumber}/approve` | Approve a workflow | `ADMIN` or `SUPPORT` |
| `POST /api/v1/workflows/{workflowNumber}/reject` | Reject a workflow | `ADMIN` or `SUPPORT` |

The configured security rules determine access. Review `SecurityConfig.java` before exposing the application publicly.

## Prerequisites

Install or configure:

- Java 21.
- PostgreSQL.
- Git.
- Maven, or use the included Maven wrapper.
- An IDE such as Eclipse or IntelliJ IDEA.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/vishwajeethshikari/EnterpriseOps-Business.git
cd EnterpriseOps-Business
```

### 2. Configure PostgreSQL

Create the database required by your local configuration.

Configure the JDBC URL, database username, and password using environment variables or another secure configuration mechanism. Make sure `application.properties` references the corresponding variables.

For example, in Windows PowerShell:

```powershell
$env:DB_PASSWORD = "your-local-database-password"
```

The variable name must match the one referenced in your application configuration.

Never commit real database credentials, JWT secrets, administrator passwords, or other secrets to Git.

### 3. Configure JWT signing

The application expects a `JWT_SECRET` environment variable containing a sufficiently long secret for HS256 signing.

Generate a cryptographically random secret and configure it in the environment used to launch Spring Boot.

For local development, PowerShell can set the variable for the current terminal session:

```powershell
$env:JWT_SECRET = "<your-generated-secret>"
```

Do not use the placeholder as a real secret. Keep the secret private and do not commit it to source control.

### 4. Configure the initial administrator

If using the initial administrator bootstrap, configure all three environment variables before starting the application:

```powershell
$env:INITIAL_ADMIN_USERNAME = "your-admin-username"
$env:INITIAL_ADMIN_EMAIL = "your-admin-email@example.com"
$env:INITIAL_ADMIN_PASSWORD = "<your-strong-unique-password>"
```

The bootstrap implementation should create the administrator only when configured and should not reset the password of an existing administrator.

Do not use weak credentials or share administrator passwords.

### 5. Run the application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The service is expected to use port `8080` unless overridden by configuration.

Flyway applies pending migrations at startup. Check the application logs for migration errors and database connectivity issues.

### 6. Open API documentation

If Springdoc is configured and the application starts successfully, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Use the documentation to inspect available endpoints, request formats, and responses.

## Authentication Examples

### Register a user

`POST /api/v1/auth/register`

```json
{
  "username": "supportuser",
  "email": "supportuser@example.com",
  "password": "your-strong-unique-password"
}
```

Public registration should assign the `USER` role automatically. Clients must not be permitted to select their own privileged role.

### Log in

`POST /api/v1/auth/login`

```json
{
  "usernameOrEmail": "supportuser@example.com",
  "password": "your-strong-unique-password"
}
```

A successful login returns a bearer access token.

### Access a protected endpoint

Send the token in the HTTP request header:

```http
Authorization: Bearer <access-token>
```

The token must be valid, unexpired, and signed with the configured secret.

## Workflow Approval

The intended approval flow separates AI investigation from business execution:

1. The Python service retrieves the relevant order information.
2. AI agents produce an investigation, policy findings, and triage proposal.
3. A workflow is created in the Java backend.
4. An authorized user reviews the workflow.
5. The user approves or rejects it.
6. If approval succeeds, the backend performs the configured business action, such as creating a support ticket.

The AI proposal itself should not bypass the approval process. Authorization, workflow-state checks, and ticket creation must be enforced by the backend.

## Integration with Python AI

The Python FastAPI service is responsible for AI orchestration and RAG. EnterpriseOps Business is responsible for business data and business operations.

This separation helps prevent the AI orchestration service from directly modifying the business database and provides a central place for request validation and authorization.

For deployment beyond local development, service-to-service authentication should also be configured where required.

## Testing and Verification

Before considering the application production-ready, verify:

- Application startup and database connectivity.
- Flyway migration success on a clean and existing database.
- User registration and duplicate-email handling.
- Login using username and email.
- BCrypt password verification.
- JWT signature and expiry validation.
- Rejection of unauthenticated requests to protected endpoints.
- Rejection of unauthorized workflow approval and rejection attempts.
- Successful approval by authorized users.
- Ticket creation only through permitted business flows.
- Automated tests for success and failure scenarios.

A successful Git commit or application compilation alone does not establish that all security controls work correctly.

## Planned Improvements

Potential improvements include:

- Comprehensive automated integration tests.
- Email verification and password reset.
- Refresh-token support and token revocation strategy.
- Audit logging for approvals and rejections.
- Stronger service-to-service authentication.
- Idempotency for workflow actions.
- Standardized API error responses.
- Docker-based local development and deployment.
- Monitoring, structured logs, and health checks.

## Project Goals

EnterpriseOps Business aims to provide a reliable Java backend for an AI-assisted customer-support and workflow-automation platform.

The project emphasizes separation of AI orchestration from business execution, secure authentication, controlled workflow operations, structured persistence, and maintainable backend architecture.

## Project Status

**Status: Active development**

Features and security controls should be considered complete only after their behavior has been tested against the current codebase and configuration.
