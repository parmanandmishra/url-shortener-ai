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
- PostgreSQL
- Lombok
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
├── docs/
├── screenshots/
├── tests/
└── README.md
```

---

# Documentation

Detailed engineering documents are available under the **docs** folder.

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

# How to Run

## Backend

```bash
mvn clean install

mvn spring-boot:run
```

Backend runs on

```
http://localhost:8080
```

---

## Frontend

```bash
npm install

npm start
```

Runs on

```
http://localhost:3000
```

---

# Swagger

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

# Testing

Run all unit tests

```bash
mvn test
```

Generate coverage

```bash
mvn verify
```

---

# Future Enhancements

Potential future improvements include:

- Redis Caching
- Distributed Short Code Generation
- Kubernetes Deployment
- Authentication & Authorization
- QR Code Generation
- Rate Limiting
- Monitoring & Observability
- Cloud Deployment
- Event-driven Analytics

---

# Assessment Focus

This project demonstrates:

- Software Architecture
- AI-assisted Software Development
- Engineering Decision Making
- Modern Java Development
- REST API Design
- Clean Code
- Testing Strategy
- Production Readiness

---
