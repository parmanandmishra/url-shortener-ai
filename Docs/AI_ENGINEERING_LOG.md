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

## Prompt 1

Objective

Requirement Analysis

Prompt

Analyze the URL Shortener problem and identify functional and non-functional requirements.

Outcome

Requirement document generated.

Decision

Accepted after refinement.

---

## Prompt 2

Objective

Task Decomposition

Prompt

Break this application into engineering tasks.

Outcome

Implementation backlog generated.

Decision

Modified implementation order.

---

## Prompt 3

Objective

Architecture

Prompt

Design a scalable Spring Boot architecture for URL Shortener.

Outcome

Layered architecture proposed.

Decision

Accepted with minor simplifications.

---

## Prompt 4

Objective

REST Controller

Prompt

Generate Spring Boot REST Controller.

Outcome

Controller generated.

Decision

Modified exception handling.

---

## Prompt 5

Objective

Repository

Prompt

Generate Spring Data JPA Repository.

Outcome

Repository generated.

Decision

Accepted.

---

## Prompt 6

Objective

Unit Tests

Prompt

Generate JUnit tests.

Outcome

Tests generated.

Decision

Added additional edge cases manually.

---

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

