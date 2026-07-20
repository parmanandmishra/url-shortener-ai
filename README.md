# AI Engineering Assessment – URL Shortener

## Author

**Name:** Parmanand Mishra

---

# Project Overview

This project implements a production-ready URL Shortener application developed using an AI-assisted Software Development Lifecycle (SDLC).

The primary objective of this assessment is to demonstrate not only software engineering capabilities but also the effective use of Generative AI throughout the software development lifecycle, including requirement analysis, solution design, implementation, testing, documentation, and engineering decision-making.

The solution follows modern engineering practices including Clean Architecture, layered design, SOLID principles, automated testing, and AI-assisted development.

---

# Engineering Philosophy

This solution was intentionally approached as an engineering exercise rather than merely a coding assignment.

While Generative AI was used to accelerate development, every architectural decision, AI-generated output, and implementation detail was critically reviewed and validated to ensure correctness, maintainability, security, and production readiness.


# Solution Features

- Create Short URL
- Redirect Short URL
- URL Validation
- Unique Short Code Generation
- Click Analytics
- URL Expiration (Optional)
- RESTful APIs
- Swagger API Documentation
- Unit Testing using JUnit & Mockito
- AI-assisted Development using GitHub Copilot & Claude

---

# Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2 (local runtime) / PostgreSQL (target architecture)
- Swagger / OpenAPI

## Frontend


## Testing

- JUnit 5
- Mockito

## AI Tools

- GitHub Copilot
- Claude

---

# Project Structure

```
url-shortener-ai/
│
├── backend/                         # Spring Boot application source code
│   ├── src/
│   ├── target/
│   └── pom.xml
│
├── Docs/                            # Assessment documentation
│   ├── images/
│   ├── AI_COMPLIANCE_REVIEW.md
│   ├── AI_ENGINEERING_LOG.md
│   ├── AMBIGUOUS_SCENARIO.md
│   ├── API_DOCUMENTATION.md
│   ├── API_GOVERNANCE.md
│   ├── API_TEST_PLAN.md
│   ├── ARCHITECTURE.md
│   ├── BROWNFIELD_SCENARIO.md
│   ├── CODE_COVERAGE.md
│   ├── ENGINEERING_APPROACH.md
│   ├── GREENFIELD_SCENARIO.md
│   ├── JAVA_COMPILATION_ERROR_ANALYSIS.md
│   ├── RELIABILITY_REVIEW.md
│   ├── REQUIREMENT_ANALYSIS.md
│   ├── TASK_DECOMPOSITION.md
│   ├── TEST_COVERAGE_REVIEW.md
│   └── TEST_RESULTS.md
│
├── postman/
│   └── URLShortener.postman_collection.json
│
├── .github/
├── .gitignore
├── pom.xml
└── README.md
```

---

# Documentation

Detailed engineering documents are available under the **Docs** folder.
| Document                               | Description                                                                                                                                             |
| -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **REQUIREMENT_ANALYSIS.md**            | Functional and non-functional requirements derived from the assessment along with project scope and deliverables.                                       |
| **TASK_DECOMPOSITION.md**              | Engineering work breakdown, implementation phases, priorities, and execution sequence.                                                                  |
| **ENGINEERING_APPROACH.md**            | Overall engineering strategy, SDLC approach, AI-assisted workflow, validation process, and quality gates.                                               |
| **ARCHITECTURE.md**                    | Solution architecture, component design, API interactions, database model, and key design decisions.                                                    |
| **GREENFIELD_SCENARIO.md**             | AI-assisted implementation of the Greenfield scenario demonstrating decomposition, implementation, and validation.                                      |
| **BROWNFIELD_SCENARIO.md**             | Brownfield impact analysis and implementation approach for enhancing the existing application with URL Expiration functionality.                        |
| **AMBIGUOUS_SCENARIO.md**              | Structured reasoning for ambiguous requirements including assumptions, risks, stakeholder questions, and engineering decisions.                         |
| **API_DOCUMENTATION.md**               | Complete REST API reference including endpoints, request/response models, validation rules, and error responses.                                        |
| **API_TEST_PLAN.md**                   | Comprehensive API testing strategy covering positive, negative, boundary, validation, and error scenarios.                                              |
| **TEST_RESULTS.md**                    | Results of executed API validation scenarios including observed responses, status codes, pass/fail status, and evidence.                                |
| **CODE_COVERAGE.md**                   | JaCoCo code coverage summary with execution details, metrics, and observations.                                                                         |
| **TEST_COVERAGE_REVIEW.md**            | Review of unit and API test coverage, identified testing gaps, and recommendations for improvement.                                                     |
| **RELIABILITY_REVIEW.md**              | Assessment of production readiness including exception handling, validation, logging, transactions, health checks, and reliability features.            |
| **API_GOVERNANCE.md**                  | API governance guidelines including REST standards, versioning, naming conventions, validation, and consistency checks.                                 |
| **JAVA_COMPILATION_ERROR_ANALYSIS.md** | Analysis of compilation/runtime issues encountered during development along with root cause and resolution.                                             |
| **AI_ENGINEERING_LOG.md**              | Complete AI engineering workflow including structured prompts, prompt traceability, AI outputs, validation, human decisions, and engineering ownership. |
| **AI_COMPLIANCE_REVIEW.md**            | Review of responsible AI usage, AI governance, traceability, validation practices, and compliance with assessment expectations.                         |

---

# AI Assisted Development

Generative AI was used throughout the Software Development Lifecycle to improve productivity while maintaining engineering quality.

AI was leveraged for:

- Requirement understanding
- Task decomposition
- Architecture brainstorming
- Code generation
- Unit test generation
- Documentation
- Code review
- Refactoring

All AI-generated outputs were manually reviewed, validated and refined before being incorporated into the final solution.

---

# Engineering Principles

The solution follows the following engineering principles:

- Clean Architecture
- SOLID Principles
- Separation of Concerns
- Layered Architecture
- RESTful API Design
- Dependency Injection
- Production-ready Coding Standards
- Test Driven Engineering
- AI-assisted Engineering

---

# Setup Instructions

## Prerequisites

- Java 21
- Maven 3.8+
- Node.js 18+ and npm (for frontend)
- Git

## 1. Clone Repository

```bash
git clone <your-repo-url>
cd url-shortener-ai
```

## 2. Backend Setup and Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend URL:

```
http://localhost:8080
```

## 3. Frontend Setup and Run

```bash
cd frontend
npm install
npm start
```

Frontend URL:

```
http://localhost:3000
```

## 4. Verify API Access

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 5. Run Tests

From `backend/`:

```bash
mvn test
```

---

# Swagger

Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

# Testing

Run all unit tests

```bash
cd backend
mvn test
```

Generate coverage

```bash
mvn verify
```

---

## Reliability Features

Implemented:
- Global exception handling using @ControllerAdvice
- Bean Validation for request validation
- Structured logging
- Health monitoring via Spring Boot Actuator
- Consistent HTTP error responses
- Transactional database operations
- Unit and API testing

Future Enhancements:
- Circuit Breaker
- Retry Policies
- Rate Limiting
- Monitoring and Alerting
- Distributed Tracing

---

# Limitations

Current implementation limitations:

1. Authentication and authorization are not implemented (open API access).
2. Rate limiting is not implemented.
3. Some invalid-request paths are still being hardened for strict `400/404/405` semantics (see `Docs/API_EXECUTION_REPORT.md`).
4. No distributed cache layer (e.g., Redis) is included yet.
5. Deployment is local/developer-focused; no production infrastructure-as-code in this repository.
6. Analytics are basic (click count focused) and not event-stream based.
7. Data retention/cleanup jobs are minimal and can be expanded for enterprise scale.

---
