# API Governance - URL Shortener

## Overview

This document defines the governance model for the URL Shortener APIs. It aligns with the layered architecture in `ARCHITECTURE.md` and the AI-assisted engineering process in `ENGINEERING_APPROACH.md`.

The goal is to keep the API design consistent, secure, testable, and release-ready.

---

## Governance Principles

| Principle | Policy |
|---|---|
| Separation of concerns | Keep controller, service, repository, and model responsibilities distinct. |
| Stateless APIs | Do not rely on server session state for API behavior. |
| Validation first | Reject malformed inputs before business logic executes. |
| Secure by default | Prevent injection, unsafe redirects, and sensitive data leakage. |
| Human oversight | AI-generated content must be reviewed before acceptance. |
| Testability | APIs must remain easy to unit test and integration test. |
| Consistency | Use consistent request and response contracts across endpoints. |

---

## Architectural Governance

The APIs must follow the layered Spring Boot architecture:

| Layer | Governance Rule |
|---|---|
| Controller | Handle HTTP, validation, and response mapping only. |
| Service | Own business rules such as short code generation, expiry checks, and click counting. |
| Repository | Encapsulate database access and query logic. |
| Entity / DTO | Keep persistence models and transfer objects distinct where appropriate. |
| Exception Handling | Centralize error mapping for predictable API responses. |

### Design Rules

- Controllers must not contain business logic.
- Services must not contain HTTP response formatting.
- Repository methods must remain focused on persistence concerns.
- Transactions must protect create/update/click-count operations.

---

## API Design Standards

| Area | Standard |
|---|---|
| Base path | Use `/api/urls` for URL management endpoints. |
| HTTP verbs | Use RESTful verbs consistently: POST, GET, PUT, DELETE. |
| Status codes | Return appropriate codes such as 201, 200, 204, 400, 404, 410, and 500. |
| Payloads | Use JSON for request and response bodies. |
| Validation | Enforce URL format, length, and required-field rules. |
| Short codes | Use 6-character alphanumeric short codes. |
| Error format | Return structured JSON errors with status, message, timestamp, and path. |

---

## Security Governance

The API security baseline is driven by the requirement to handle invalid and malicious input safely.

| Control Area | Governance Requirement |
|---|---|
| Input validation | Validate URLs, path parameters, and payloads before processing. |
| Injection defense | Prevent SQL injection, script injection, and command injection. |
| Sensitive data | Do not expose stack traces, secrets, or database internals. |
| Redirect safety | Avoid open redirect behavior unless explicitly approved. |
| Error handling | Return safe error messages through centralized handlers. |
| Authentication | Current assessment scope does not require authentication, but future extension must add it deliberately. |

---

## Testing Governance

| Test Type | Governance Expectation |
|---|---|
| Unit testing | Cover service, controller, repository, validation, and exception behavior. |
| Integration testing | Verify API and database behavior together. |
| Validation testing | Confirm missing, null, blank, malformed, and oversized inputs fail correctly. |
| Security testing | Validate injection, large payload, and unsafe redirect cases. |
| Performance testing | Confirm URL creation and redirect latency targets are met. |
| Regression testing | Re-run critical create, retrieve, redirect, update, and delete flows after changes. |
| Smoke testing | Verify application startup and core endpoints before release. |

### Evidence Expectations

- Tests must be repeatable.
- Failures must be traceable to a specific scenario.
- Coverage must include the requirements listed in `REQUIREMENT_ANALYSIS.md`.

---

## AI Governance

AI is used as an accelerator, not an authority.

| Area | Governance Rule |
|---|---|
| Planning | AI may help structure scenarios and documentation. |
| Implementation | AI-generated code must be reviewed and corrected by a human engineer. |
| Testing | AI may assist with test ideas, but human review is mandatory. |
| Failure analysis | AI may suggest likely causes, but human validation is required. |
| Approval | Final correctness and production readiness remain human-owned. |

### Required Statement

- AI generated the initial artifacts.
- Human engineer reviewed every test.
- Human engineer executed every test.
- Human engineer approved all results.
- Engineer retained full ownership of correctness and production readiness.

---

## Quality Gates

The following gates must pass before the solution is considered complete:

| Gate | Criteria |
|---|---|
| Requirements coverage | Functional and non-functional requirements are mapped to APIs and tests. |
| Architecture alignment | Implementation follows the layered design documented in `ARCHITECTURE.md`. |
| Code review | All AI-assisted and human-written changes are reviewed. |
| Test completion | Unit, integration, validation, security, and regression tests are executed. |
| Performance | URL creation and redirect timing targets are met. |
| Security | Core injection and data-exposure risks are addressed. |
| Maintainability | Coding standards and clean layering are preserved. |
| Deployment readiness | Build, configuration, and environment setup are suitable for controlled release. |

---

## Release and Deployment Governance

| Area | Policy |
|---|---|
| Build | Maven build must succeed before release. |
| Environment | PostgreSQL-backed test or deployment environment must be available. |
| Documentation | API documentation and test plan must remain synchronized with implementation. |
| Observability | Logging, validation errors, and health endpoints must support troubleshooting. |
| Rollout | Deploy in a controlled manner with rollback awareness. |

---

## Change Management

- Changes to API behavior must be reflected in the documentation and test plan.
- New endpoints must follow the same layering and validation rules.
- Breaking changes require explicit review and sign-off.
- Any change that affects security, expiry, or click counting must include regression coverage.
