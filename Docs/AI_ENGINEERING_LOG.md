# AI Engineering Log

## Objective

This document captures how Generative AI was used across the SDLC for the URL Shortener assessment. AI was used as an accelerator, while design, implementation decisions, and production-readiness accountability remained with the human engineer.

---

## AI Tools

- GitHub Copilot
- Claude

---

## AI Usage Summary

| SDLC Activity | AI Used | Human Review |
|---|---|---|
| Requirement Analysis | Yes | Yes |
| Task Decomposition | Yes | Yes |
| Architecture Brainstorming | Yes | Yes |
| Entity/DTO/Controller Drafting | Yes | Yes |
| Unit Test Drafting | Yes | Yes |
| API Documentation Drafting | Yes | Yes |
| Final Validation & Approval | No | Human Only |

---

## Prompt Engineering Standard (Applied)

Each prompt follows the structure below for repeatability and auditability:

1. **Role** (e.g., Principal QA Architect, Java Architect)
2. **Objective**
3. **Context** (stack, constraints, files)
4. **Scope** (what to include / exclude)
5. **Acceptance Criteria** (measurable outcomes)
6. **Output Contract** (file path, markdown/json format)
7. **Security Constraints** (no secrets, no unsafe code, no external leaks)
8. **Traceability Tag** (`PRM-xx`)

---

## Prompt Traceability Register

| ID | Prompt Goal | Key Inputs/References | Output Artifact(s) | Human Decision |
|---|---|---|---|---|
| PRM-01 | Generate requirement analysis | Assessment brief | `Docs/REQUIREMENT_ANALYSIS.md` | Accepted with refinement |
| PRM-02 | Generate engineering task decomposition | Requirements doc | Backlog/task sequence | Re-ordered by engineer |
| PRM-03 | Draft engineering approach | Requirements + architecture expectations | `Docs/ENGINEERING_APPROACH.md` | Accepted with additions |
| PRM-04 | Draft scalable architecture | Spring Boot constraints | `Docs/ARCHITECTURE.md` | Accepted with simplifications |
| PRM-05 | Generate REST controller draft | API scope + HTTP rules | Controller implementation draft | Accepted with fixes |
| PRM-06 | Generate domain/entity model drafts | JPA + DB constraints | Entity/DTO drafts | Partially accepted |
| PRM-07 | Generate service layer draft | SOLID + validation + transactions | Service implementation draft | Accepted with corrections |
| PRM-08 | Generate unit tests | Existing code + test plan | JUnit5/Mockito test classes | Accepted after review |
| PRM-09 | Generate API documentation | Implemented APIs only | `Docs/API_DOCUMENTATION.md` | Accepted with edits |
| PRM-10 | Generate enterprise API test plan | Requirements + architecture + API docs | `Docs/API_TEST_PLAN.md` | Accepted with edits |
| PRM-11 | Execute API validation scenarios | Running endpoints | `Docs/API_EXECUTION_REPORT.md` | Accepted (defects logged) |
| PRM-12 | Analyze runtime/JMX exception | Error logs + runtime context | Troubleshooting write-up + config updates | Accepted |
| PRM-13 | Analyze compilation/initialization issue | Build error logs | RCA and remediation actions | Accepted |
| PRM-14 | Identify coverage gaps and add tests only | Codebase + existing tests | New focused tests + `Docs/CODE_COVERAGE.md` | Accepted |

---

## Structured Prompt Log

### PRM-01 — Requirement Analysis
**Objective:** Create structured requirements documentation.  
**Prompt Pattern:** Role + Context + Required sections + Output file.  
**Mandatory Sections Requested:** Functional, non-functional, constraints, acceptance criteria, AI usage expectations.  
**Prompt:**  
Act as a Senior Product/Engineering Analyst. Analyze the assessment brief and generate `Docs/REQUIREMENT_ANALYSIS.md` with: business goals, functional requirements, non-functional requirements, constraints, acceptance criteria, dependencies, and assumptions. Use only repository context, include traceable requirement IDs, do not add secrets or external proprietary content.  
**Output:** `Docs/REQUIREMENT_ANALYSIS.md`  
**Decision:** Accepted after review and refinement.

### PRM-02 — Task Decomposition
**Objective:** Generate execution-ready engineering task breakdown.  
**Prompt Constraints:** Dependency-aware sequencing, clear ownership boundaries, test-first checkpoints.  
**Prompt:**  
Act as an Engineering Manager. Decompose `Docs/REQUIREMENT_ANALYSIS.md` into an implementation backlog with phases, task IDs, dependencies, risk notes, and validation gates. Keep tasks atomic and verifiable. Use only project files and do not propose unsafe shortcuts.  
**Output:** Backlog/task sequence  
**Decision:** Re-ordered by engineer.

### PRM-03 — Engineering Approach
**Objective:** Produce enterprise engineering approach.  
**Prompt Constraints:** Include Agile, SDLC, AI-assisted development, coding standards, testing strategy, deployment, quality gates.  
**Prompt:**  
Act as a Principal Engineer. Produce `Docs/ENGINEERING_APPROACH.md` covering SDLC model, branching/review process, coding standards, test strategy, release controls, quality gates, and AI-assistance governance. Keep it implementation-aligned and auditable, with no speculative architecture beyond repository scope.  
**Output:** `Docs/ENGINEERING_APPROACH.md`  
**Decision:** Accepted with gap closure and deduplication.

### PRM-04 — Architecture
**Objective:** Produce scalable architecture design.  
**Prompt Constraints:** HLD, LLD, sequence flow, components, security, scalability; layered/SOLID patterns.  
**Prompt:**  
Act as a Software Architect. Generate `Docs/ARCHITECTURE.md` with HLD, LLD, component diagram narrative, request/control flow, key design decisions, and scalability/security considerations for this Spring Boot URL shortener. Reflect only implemented or clearly marked planned capabilities.  
**Output:** `Docs/ARCHITECTURE.md`  
**Decision:** Accepted with added explicit sections.

### PRM-05 — REST Controller Draft
**Objective:** Generate API controller draft aligned to REST semantics.  
**Prompt Constraints:** Correct mappings, validation entry points, explicit status codes, no business logic leakage.  
**Prompt:**  
Act as a Senior Spring Boot Engineer. Draft controller endpoints for URL creation, lookup, redirect, analytics, update, and delete using `ResponseEntity` and explicit HTTP status handling. Keep controller thin, delegate logic to service, and avoid embedding persistence logic.  
**Output:** Controller implementation draft  
**Decision:** Accepted with fixes.

### PRM-06 — Domain and DTO Model Drafts
**Objective:** Generate entity/DTO contracts for API and persistence layers.  
**Prompt Constraints:** JPA-safe mappings, validation-ready DTOs, backward-compatible field semantics.  
**Prompt:**  
Act as a Domain Model Architect. Draft JPA entity and DTO models for URL mappings, including required fields, lifecycle timestamps, click count, and optional expiry behavior. Add validation annotations where applicable and ensure field naming aligns across controller/service/repository layers.  
**Output:** Entity/DTO drafts  
**Decision:** Partially accepted.

### PRM-07 — Service Layer Draft
**Objective:** Generate business service implementation with reliability controls.  
**Prompt Constraints:** Validation, normalization, transaction boundaries, short-code uniqueness, exception-driven flows.  
**Prompt:**  
Act as a Principal Java Engineer. Implement service-layer logic for URL normalization/validation, unique short-code generation, create/read/update/delete operations, redirect click counting, and expiry checks. Use transactional boundaries and throw domain exceptions for invalid/not-found/expired states.  
**Output:** Service implementation draft  
**Decision:** Accepted with corrections.

### PRM-09 — API Documentation
**Objective:** Document only implemented APIs.  
**Prompt Constraints:** Include endpoint contract, status codes, validation rules, error responses, Swagger/OpenAPI details.  
**Prompt:**  
Act as a Technical Writer for backend APIs. Update `Docs/API_DOCUMENTATION.md` to match current implementation exactly (no fictional endpoints). Include request/response examples, status codes, validation rules, and known behavior notes with traceability to tests.  
**Output:** `Docs/API_DOCUMENTATION.md`  
**Decision:** Accepted with requirements traceability updates.

### PRM-08 — Unit Tests
**Objective:** Generate targeted JUnit5/Mockito coverage for critical paths.  
**Prompt Constraints:** No production code changes; focus on behavior, exception mapping, boundary inputs.  
**Prompt:**  
Act as a Senior Test Engineer. Add missing JUnit5 + Mockito tests for service/controller/exception layers to improve confidence in validation, error paths, and expiry behavior. Do not modify production code; use deterministic test data and explicit assertions.  
**Output:** JUnit5/Mockito test classes  
**Decision:** Accepted after review.

### PRM-10 — API Test Plan
**Objective:** Produce enterprise-grade API test plan.  
**Prompt Constraints:** Positive, negative, boundary, validation, OWASP security, performance, DB validation, RTM, AI-assisted testing section.  
**Prompt:**  
Act as a Principal QA Architect. Generate `Docs/API_TEST_PLAN.md` with end-to-end API test strategy, scenario matrix, validation/negative/security/performance coverage, database checks, entry/exit criteria, deliverables, and requirement traceability. Keep scenarios actionable and measurable.  
**Output:** `Docs/API_TEST_PLAN.md`  
**Decision:** Accepted with expanded traceability.

### PRM-11 — API Execution Validation
**Objective:** Discover and execute API scenarios with realistic data and report pass/fail reasons.  
**Prompt Constraints:** No application code changes during execution run; produce markdown execution evidence.  
**Prompt:**  
Act as a Senior QA Engineer. Execute discovered API scenarios against running endpoints using realistic payloads and record observed request, response body, status code, pass/fail, and failure reason. Update execution report in markdown with only observed evidence; do not infer unavailable results.  
**Output:** `Docs/API_EXECUTION_REPORT.md`  
**Decision:** Accepted; defects tracked from observed behavior.

### PRM-12  — Exception Analysis
**Objective:** Perform structured RCA on runtime/compile issues.  
**Prompt Constraints:** Multi-cause analysis, confidence levels, verification steps, no speculative rewrites.  
**Output:** Troubleshooting analysis and remediations logged in working session/docs.  
**Decision:** Accepted after verification.

### PRM-13  — Review Reliability Features
**Objective:** Verify reliability features are implemented.
**Prompt Constraints:** Review for global exception handling, input validation, structured logging, health endpoint, proper HTTP status codes, transaction management, idempotent behavior, unit tests, API tests.
**Prompt:** 
Act as a Principal Java Architect.

Review my Spring Boot project and verify whether the following reliability features are implemented:

- Global exception handling
- Input validation
- Structured logging
- Health endpoint (Spring Boot Actuator)
- Proper HTTP status codes
- Transaction management
- Idempotent behavior (where applicable)
- Unit tests
- API tests

For each feature provide:

1. Status (Implemented / Partially Implemented / Not Implemented)
2. Evidence (class/file/method)
3. Comments
4. Recommendation (if missing)

Generate a markdown report named Docs/RELIABILITY_REVIEW.md.

Do not modify any code.
**Decision:** Accepted after verification.

---

## Secure AI Usage Controls

The following controls were applied to all AI interactions:

1. **No secrets in prompts**: No credentials, tokens, private keys, or production connection strings were shared.
2. **Repository-scoped context only**: Prompts referenced local project files and observed logs only.
3. **No third-party code ingestion**: No proprietary external source code was copied into prompts.
4. **Principle of least disclosure**: Only the minimum context needed for each task was provided.
5. **Human approval gate**: AI output was never auto-merged; all changes were reviewed before acceptance.
6. **Security-first review**: Generated API/error handling/test artifacts were reviewed against OWASP/API security expectations.
7. **No blind execution**: AI suggestions were validated through tests and runtime checks before being trusted.

---

## AI Productivity

| Activity | Estimated Improvement |
|---|---|
| Requirement Analysis | 30% |
| Architecture Brainstorming | 25% |
| Boilerplate Code | 70% |
| Unit Test Generation | 60% |
| Documentation | 75% |
| Refactoring Support | 40% |

**Overall Productivity Improvement:** Approximately **45–50%**

---

## Human Validation

Every AI-generated artifact was reviewed for:

- Correctness
- Security
- Maintainability
- Performance
- Readability
- Coding standards compliance

No AI-generated change was accepted without human review.

---

## AI Mistakes and Engineering Corrections

| AI Suggestion | Issue Found | Engineering Decision |
|---|---|---|
| Generated random short code without robust uniqueness guarantees | Collision risk | Added unique DB constraint + retry logic |
| Missed strict URL validation behavior | Security and data quality risk | Added stronger validation/normalization and tests |
| Generated generic exception handling | Poor API error contract | Implemented structured `@ControllerAdvice` responses |

---
