
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

# Problem Statement and Context

The assessment problem statement and business goal are defined in `Docs/REQUIREMENT_ANALYSIS.md`. This engineering document does not repeat that content; instead, it builds on it by describing how the solution is approached, validated, and delivered.

In short, the implementation is designed to support URL shortening, persistence, retrieval, redirection, validation, and optional expiry while preserving testability, maintainability, and clear AI oversight.

---

# Goals and Non-Goals

## Goals

- Deliver a stable REST-based URL shortener.
- Demonstrate AI-assisted development with human review.
- Keep the codebase maintainable and testable.
- Support secure validation and reliable persistence.

## Non-Goals

- Authentication and authorization flows.
- Distributed caching or multi-region deployment.
- QR code generation, billing, or admin workflows.
- Large-scale platform operationalization beyond assessment needs.

---

# Requirements and Constraints

## Requirements

- Create, store, retrieve, redirect, update, and delete shortened URLs.
- Validate URLs and reject malformed or unsupported input.
- Preserve data integrity and unique short codes.
- Support unit and integration testing.

## Constraints

- Limited assessment time.
- AI-generated outputs must be reviewed before acceptance.
- PostgreSQL is used for persistence.
- The solution should stay simple while still demonstrating enterprise engineering quality.

---

# Proposed Approach and Architecture

The solution follows a layered Spring Boot architecture:

- Controller layer for HTTP handling.
- Service layer for business logic.
- Repository layer for persistence.
- Model / DTO layer for request and response contracts.

This approach aligns with the architecture documentation and keeps concerns separated for testability and future change. The application uses REST APIs, dependency injection, and a relational database to support clean, maintainable implementation.

---

# Alternatives Considered

Several implementation choices were considered before settling on the final approach:

| Alternative | Why It Was Not Chosen |
|---|---|
| Single-layer or monolithic implementation | Harder to test, maintain, and extend. |
| In-memory storage only | Not suitable for persistence or realistic assessment expectations. |
| Heavy microservices split | Too much overhead for the scope of the assessment. |
| AI-only implementation with minimal review | Too risky for correctness, security, and production readiness. |
| Distributed cache first | Not required for the current requirements and increases complexity. |

The layered Spring Boot approach was selected because it provides the best balance of clarity, maintainability, and assessment fit.

---

# Trade-offs

The engineering approach intentionally prioritizes clarity and reliability over premature optimization.  
Key trade-offs accepted for this assessment are:

| Decision | Benefit | Trade-off |
|---|---|---|
| Layered architecture over minimal single-layer design | Better separation of concerns, testability, and maintainability | Slightly more boilerplate and call-path overhead |
| Relational persistence over in-memory-only storage | Durable, queryable, and realistic data model | More setup/configuration complexity |
| Strong validation and explicit error handling | Safer API contracts and predictable behavior | Additional implementation effort and broader test surface |
| AI-assisted generation with mandatory human review | Faster delivery with quality control | Review/refinement overhead reduces raw speed |
| Keep scope focused (no auth/rate limiting/microservices in core) | Faster completion of high-priority requirements | Some enterprise capabilities deferred to future phases |
| Optional expiry support with nullable field | Backward compatibility and flexible business behavior | Extra branching logic and edge-case testing |

These trade-offs were chosen to maximize assessment value while maintaining production-oriented engineering discipline.

---

# Rollout, Validation, and Testing

Rollout is treated as a controlled assessment deployment rather than a full production release.

## Rollout

- Build and package with Maven.
- Deploy against a configured PostgreSQL instance.
- Verify Swagger/OpenAPI and API endpoints are reachable.

## Validation

- Confirm request/response contracts.
- Validate persistence and database state.
- Review AI-generated content manually before acceptance.

## Testing

- Unit testing for isolated business logic.
- Integration testing for API and database interaction.
- Validation testing for bad input and schema rules.
- Regression and smoke testing for release confidence.

---

# Quality Gates

The solution must meet the following quality gates before being considered complete:

| Gate | Criteria |
|---|---|
| Requirements coverage | Functional requirements from `REQUIREMENT_ANALYSIS.md` are addressed. |
| Architecture alignment | Implementation follows the layered design in `ARCHITECTURE.md`. |
| Code review | AI-generated and human-written changes are reviewed before acceptance. |
| Test completion | Unit, integration, validation, and regression checks are executed. |
| CI automation gate | GitHub Actions workflow (`.github/workflows/quality-gates.yml`) runs Maven tests, package build, actuator health verification, and Newman API collection run. |
| Security review | Input validation and injection risks are addressed. |
| HTTP contract safety | Explicit exception mappings are enforced for malformed JSON, method mismatch, media-type mismatch, not-found, and expired-resource conditions. |
| Maintainability | Code follows the documented coding standards. |
| Deployment readiness | Build and runtime configuration are suitable for controlled deployment. |

These gates provide a clear pass/fail boundary for engineering quality and release confidence.

---

# Agile Delivery Approach

The assessment was approached using an Agile mindset with iterative delivery, continuous refinement, and frequent validation of requirements and outputs.

Key Agile practices applied:

- Short planning cycles with incremental progress.
- Prioritization of high-value features and risks first.
- Continuous review of AI-generated artifacts before acceptance.
- Regular validation of implementation against requirements.
- Adaptation of design and tests as understanding improved.

This Agile approach ensured the solution remained responsive to requirement clarification while preserving engineering discipline.

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

# Testing Strategy

The testing strategy is designed to verify functionality, reliability, and resilience before release.

Core testing layers:

- Unit testing for isolated business logic.
- Integration testing for API and database interaction.
- Validation testing for request and response rules.
- Exception testing for controlled failure handling.
- Regression testing for core user flows.
- Smoke testing for rapid release confidence.

This strategy ensures the solution is testable, repeatable, and suitable for quality assurance review.

---

# Coding Standards

The implementation follows structured coding standards to keep the solution maintainable and reviewable.

Standards applied:

- Consistent naming conventions for classes, methods, and variables.
- Separation of concerns across controller, service, repository, and model layers.
- Clear validation and exception handling.
- Use of dependency injection rather than hard-coded dependencies.
- Small, focused methods with readable logic.
- Preferential use of established framework conventions.
- Testable design with mockable dependencies.

These standards support long-term maintainability and reduce the risk of AI-generated inconsistency.

---

# Deployment Considerations

The solution is designed for straightforward deployment in a Spring Boot environment.

Deployment assumptions and practices:

- Maven-based build and packaging.
- Configuration externalized for environment-specific values.
- PostgreSQL used as the backing store.
- Swagger/OpenAPI available for API discovery and verification.
- Logging and validation enabled for operational support.
- Suitable for local, test, and future production-style deployment paths.

Although this assessment does not require full production rollout, the design supports deployment readiness and environment portability.

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
