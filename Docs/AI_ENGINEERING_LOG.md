# AI Engineering Log

## Objective

This document captures how Generative AI was used throughout the Software Development Lifecycle.

AI was used as an engineering accelerator rather than an autonomous developer.

All generated outputs were reviewed and validated before implementation.

---

# AI Tools

- GitHub Copilot
- Claude

---

# AI Usage Summary

| SDLC Activity | AI Used | Human Review |
|--------------|----------|--------------|
| Requirement Analysis | Yes | Yes |
| Task Decomposition | Yes | Yes |
| Architecture Brainstorming | Yes | Yes |
| Entity Generation | Yes | Yes |
| DTO Generation | Yes | Yes |
| Repository Generation | Yes | Yes |
| Controller Generation | Yes | Yes |
| Unit Tests | Yes | Yes |
| Documentation | Yes | Yes |
| Final Validation | No | Human Only |

---

# AI Prompt Log

## Prompt 1 - Generate Requirement Document

Objective

Requirement Analysis

Prompt

Read the assessment requirements and create a structured requirement analysis.

Separate:

- Functional Requirements
- Non Functional Requirements
- Constraints
- Acceptance Criteria
- AI Usage Expectations

Output
Docs/
01_REQUIREMENT_ANALYSIS.md

Outcome

Requirement document generated.

Decision

Accepted after refinement.

---

## Prompt 2 Generate Task Decomposition

Objective

Task Decomposition

Prompt

Break this application into engineering tasks.

Outcome

Implementation backlog generated.

Decision

Modified implementation order.

---

## Prompt 3 - Design Engineering Approach

Create Engineering Approach for this assessment.

Include

- Agile
- SDLC
- AI Assisted Development
- Coding Standards
- Testing Strategy
- Deployment


## Prompt 4 - Design Scalable Architecture



Design scalable Spring Boot architecture.

Use

Hexagonal Architecture

SOLID

Layered Design

Repository Pattern

DTO Pattern

Outcome

Layered architecture proposed.

Decision

Accepted with minor simplifications.

---

## Prompt 5 - Generate REST Controller

Intent

Generate REST Controller.

Constraints

Prompt
Generate REST Controller.

Use
- REST conventions
- Proper HTTP Status codes
- Validation
- Swagger annotations
- Exception handling

Acceptance Criteria

- POST /shorten
- GET /{code}

Outcome

Controller generated.

Decision

Accepted with minor adjustments.

---

## Prompt 6 - Generate JPA entity for URL Mapping

Objective

Repository

Prompt

Generate JPA entity for URL Mapping.

Requirements

UUID id

originalUrl

shortCode

expiryDate

clickCount

createdAt

Use Lombok

Use Validation

Java 21

Outcome

Repository generated.

Decision

Accepted.

---

## Prompt 6 – Generate Domain Model

Intent:
Generate entities for URL Shortener.

Context:
Spring Boot 3.5
Java 21
JPA
PostgreSQL

Constraints

- Follow SOLID principles
- Use Lombok
- Use UUID as primary key
- Validate URL format

Acceptance Criteria

- Clean JPA entities
- Production-ready
- No business logic


## Prompt 7 –Generate Service Layer.

Prompt
Generate Service Layer.

Requirements

Logging

Validation

Custom Exceptions

Transaction Management

SOLID


## Prompt 9 - Code Quality & Architecture Review

Review this code as if you are a Principal Java Architect.
Suggest
Security
Performance
Readability
Maintainability
Spring Boot best practices
Do not rewrite unless necessary.


## Prompt 9 - Generate API documentations

Analyze my Spring Boot project and generate a professional API_DOCUMENTATION.md.

Document only the implemented REST APIs.

For each API include:
- Purpose
- HTTP Method
- Endpoint
- Request
- Response
- Status Codes
- Validation Rules
- Sample Request & Response
- Error Responses

Also include:
- API Overview
- Authentication
- Error Handling
- Versioning
- Swagger/OpenAPI information

Generate clean markdown suitable for Docs/API_DOCUMENTATION.md.


## Prompt 10 - Generate API Test Plan

Generate enterprise API Test Plan.

Include

Positive

Negative

Boundary

Security

Performance

Validation

OWASP


## Prompt 8 - Generate Unit Tests

Generate JUnit5 tests.

Use Mockito.

Cover

Positive

Negative

Boundary

Repository Failure


---

## Prompt 8 - Additional API Testing

You are a Principal QA Architect.

Objective:
Validate the URL Shortener application using AI-assisted testing.

Tasks:
1. Discover all REST APIs.
2. Execute:
- Positive Tests
- Negative Tests
- Boundary Tests
- Validation Tests


## Prompt 8 - Code Coverage Analysis

Analyze my Spring Boot project and identify classes with low or no test coverage.

Generate only the missing JUnit 5 and Mockito tests to achieve at least 80% line coverage.

Do not modify production code.





# AI Productivity

| Activity | Estimated Improvement |
|-----------|----------------------|
| Requirement Analysis | 30% |
| Architecture Brainstorming | 25% |
| Boilerplate Code | 70% |
| Unit Test Generation | 60% |
| Documentation | 75% |
| Refactoring | 40% |

Overall Productivity Improvement

Approximately **45–50%**

---

# Human Validation

Every AI-generated artifact was reviewed for

- Correctness
- Security
- Maintainability
- Performance
- Readability
- Coding Standards

No AI-generated code was accepted without review.

---

# Key Engineering Learning

Generative AI significantly improves engineering productivity when combined with

- Structured problem decomposition
- Strong architectural thinking
- Human engineering judgement
- Continuous validation

# AI Mistakes & Corrections

| AI Suggestion                                        | Issue Found        | Engineering Decision                                              |
| ---------------------------------------------------- | ------------------ | ----------------------------------------------------------------- |
| Generated random short code without uniqueness check | Risk of collisions | Added database unique constraint and retry mechanism              |
| Missed URL validation                                | Security risk      | Added validation using `@URL` and custom validator                |
| Generated generic exception handling                 | Poor API design    | Implemented `@ControllerAdvice` with meaningful HTTP status codes |


## Prompt 12 - Exception Analysis
You are a Principal Java Architect and Production Support Engineer.

Analyze the following exception as if this occurred in a production Spring Boot application.

Application Details:
- Java 21
- Spring Boot 3.5
- Maven
- IntelliJ IDEA
- PostgreSQL
- REST APIs
- Running locally on macOS

Exception:

java.io.IOException: The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.
at jdk.management.agent/sun.management.jmxremote.LocalRMIServerSocketFactory$1.accept(LocalRMIServerSocketFactory.java:114)
at java.rmi/sun.rmi.transport.tcp.TCPTransport$AcceptLoop.executeAcceptLoop(TCPTransport.java:424)
at java.rmi/sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:388)
at java.base/java.lang.Thread.run(Thread.java:1583)

Please provide:

1. Executive Summary (2-3 lines)
2. Root Cause Analysis
3. Why this error occurs
4. Is it a framework issue, JVM issue, Spring Boot issue, IntelliJ issue, or application code issue?
5. Is this error blocking application functionality or only a warning?
6. Common scenarios where this happens.
7. How to reproduce it.
8. Step-by-step troubleshooting process.
9. Recommended fixes (ordered from most likely to least likely).
10. How to verify the fix.
11. Best practices to avoid this issue in production.
12. Whether this issue can be safely ignored during local development.
13. Confidence level for each possible root cause.

Do not immediately assume a single cause.
Provide reasoning for each possible cause and explain how to validate it.
Return the answer in a structured markdown format suitable for documenting in an engineering troubleshooting guide.

## Prmopt 13 - Exception Analysis

Analyze this compilation error.
“java: java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN”

Suggest root cause.
Do not rewrite code.
Only explain issue.
