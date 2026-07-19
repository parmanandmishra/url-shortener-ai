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

- React
- Material UI

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
├── backend/
├── frontend/
├── Docs/
├── screenshots/
├── tests/
└── README.md
```

---

# Documentation

Detailed engineering documents are available under the **Docs** folder.

| Document | Description |
|----------|-------------|
| Engineering Approach | Overall implementation strategy and AI-assisted development approach |
| Requirement Analysis | Functional and Non-Functional requirements |
| Task Decomposition | Breakdown of implementation tasks |
| Solution Architecture | Architecture, APIs, database design and design decisions |
| AI Engineering Log | AI prompts, generated outputs, validation and engineering decisions |
| Greenfield Scenario | AI-assisted development for a new feature |
| Brownfield Scenario | AI-assisted enhancement to an existing system |
| Ambiguous Requirements | Requirement clarification and engineering assumptions |

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
