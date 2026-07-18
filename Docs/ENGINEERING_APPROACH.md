
# Engineering Approach

## Overview

This assessment has been approached as an engineering exercise rather than simply a software development assignment.

While the application itself is a URL Shortener, the primary objective is to demonstrate how Artificial Intelligence can be effectively integrated into the Software Development Lifecycle (SDLC) while maintaining engineering quality, architectural integrity, and human oversight.

The implementation focuses on balancing AI-assisted productivity with engineering judgement, ensuring that every AI-generated artifact is reviewed, validated, and refined before becoming part of the final solution.

---

# Development Philosophy

The solution was developed based on the following principles:

- AI should accelerate engineering, not replace engineering judgement.
- Architecture should be designed before implementation.
- Requirements should be clearly understood before coding.
- Problems should be decomposed into smaller, manageable tasks.
- AI-generated code must always be reviewed before acceptance.
- Code should remain maintainable, testable, and production ready.

---

# AI Assisted Software Development Lifecycle

The project follows an AI-assisted SDLC where Generative AI is used throughout different stages of software engineering.

```
Requirement Analysis
        │
        ▼
Task Decomposition
        │
        ▼
Architecture Design
        │
        ▼
AI Assisted Implementation
        │
        ▼
Human Validation
        │
        ▼
Testing
        │
        ▼
Documentation
```

AI was intentionally used as an engineering assistant throughout the lifecycle rather than as an autonomous developer.

---

# Engineering Process

The implementation was executed using the following phases.

## Phase 1 – Requirement Analysis

Before writing any code, the requirements were analyzed to identify:

- Functional requirements
- Non-functional requirements
- Constraints
- Risks
- Assumptions
- Success criteria

This helped establish a clear understanding of the expected solution.

---

## Phase 2 – Task Decomposition

The problem was broken into smaller engineering tasks.

Instead of asking AI to generate the complete application, each logical component was developed independently.

Example:

- Domain Model
- Database Design
- REST APIs
- Business Services
- Repository Layer
- Unit Tests
- Documentation

This approach significantly improved AI output quality.

---

## Phase 3 – Solution Design

Before implementation, the following design decisions were made:

- Layered Architecture
- RESTful APIs
- Relational Database
- Stateless Services
- Dependency Injection
- Clean Separation of Concerns

This ensured that implementation followed a predefined architecture rather than evolving organically during coding.

---

## Phase 4 – AI Assisted Implementation

Generative AI tools (GitHub Copilot and Claude) were used to accelerate software development.

Typical use cases included:

- Boilerplate code generation
- DTO creation
- Repository implementation
- REST controller generation
- Unit test generation
- Documentation
- Refactoring suggestions

Each generated output was manually reviewed and improved before acceptance.

---

## Phase 5 – Validation

Engineering validation was performed for every AI-generated artifact.

Validation included:

- Functional correctness
- Code quality
- Readability
- Maintainability
- Security considerations
- Error handling
- Coding standards

Only validated code was incorporated into the final solution.

---

## Phase 6 – Testing

The solution includes:

- Unit Testing
- Integration Testing
- Exception Testing
- API Validation

Testing ensures that AI-generated implementations behave as expected.

---

# AI Usage Strategy

Generative AI was used in areas where it provides the highest productivity gains.

| SDLC Phase | AI Usage |
|------------|----------|
| Requirement Understanding | Yes |
| Task Decomposition | Yes |
| Architecture Brainstorming | Yes |
| Boilerplate Code | Yes |
| Business Logic | Partially |
| Unit Test Generation | Yes |
| Documentation | Yes |
| Code Review | Yes |
| Final Validation | Human Only |

Engineering judgement was retained throughout the implementation.

---

# Human Oversight

AI-generated outputs were never accepted without review.

Each generated artifact was evaluated for:

- Correctness
- Security
- Scalability
- Maintainability
- Readability
- Performance

Engineering decisions always remained under human control.

---

# Design Principles

The solution follows industry-standard engineering practices including:

- SOLID Principles
- Clean Architecture
- Layered Design
- Separation of Concerns
- Dependency Injection
- RESTful API Design
- Testability
- Reusability

---

# Production Readiness

Although this is an assessment project, the solution has been designed with production readiness in mind.

Key considerations include:

- Modular architecture
- Exception handling
- Validation
- Logging support
- Future scalability
- Database indexing
- API documentation
- Automated testing

---

# Risk Management

The following engineering risks were considered during implementation.

| Risk | Mitigation |
|------|------------|
| Duplicate short URLs | Unique database constraint |
| Invalid URLs | Input validation |
| AI hallucinations | Human review |
| Poor maintainability | Clean architecture |
| Future scalability | Stateless design |

---

# Engineering Decisions

Several technology decisions were made before implementation.

| Decision | Rationale |
|-----------|-----------|
| Spring Boot | Enterprise-grade REST framework with excellent ecosystem support |
| React | Lightweight and component-based frontend |
| PostgreSQL | Reliable relational database with ACID compliance |
| Layered Architecture | Improves maintainability and separation of concerns |
| JUnit & Mockito | Standard testing framework for Java applications |
| GitHub Copilot & Claude | Accelerate implementation while retaining engineering control |

---

# Lessons Learned

This assessment reinforces an important engineering principle:

> **The value of Generative AI is not measured by how much code it generates, but by how effectively engineers can guide, validate, and integrate its output into high-quality software systems.**

AI significantly improves engineering productivity when combined with structured problem decomposition, architectural thinking, and disciplined human validation.

---

# Conclusion

This solution demonstrates an AI-assisted engineering workflow that combines modern software development practices with responsible use of Generative AI.

The objective was not only to build a functional application, but also to showcase an engineering process that is scalable, maintainable, and aligned with enterprise software development standards.
