# API Test Plan - URL Shortener

## 1. Executive Summary

This test plan defines the QA strategy for the Spring Boot URL Shortener application. The objective is to verify that the APIs create, store, retrieve, redirect, update, and delete shortened URLs correctly while meeting enterprise expectations for correctness, security, and performance.

### Purpose

- Validate business functionality for URL shortening and redirect flows.
- Verify API behavior under normal, invalid, and edge-case inputs.
- Confirm database integrity, click tracking, and response correctness.
- Establish a repeatable test strategy suitable for enterprise delivery.

### Testing Objectives

- Ensure valid URLs return a unique short code and are persisted successfully.
- Validate redirect and analytics behavior for known, unknown, and expired URLs.
- Detect input validation defects, security weaknesses, and data integrity issues.
- Confirm performance targets for URL creation and redirection.

### Scope

- `POST /api/urls/shorten`
- `GET /api/urls/{shortCode}`
- `GET /api/urls/redirect/{shortCode}`
- `GET /api/urls/analytics/{shortCode}`
- `PUT /api/urls/{id}`
- `DELETE /api/urls/{id}`

### Out of Scope

- User registration and login.
- OAuth/API key authentication.
- Distributed caching and multi-region deployment.
- QR code generation and billing.

### Assumptions

- Base URL is `http://localhost:8080`.
- Public access is allowed for current APIs.
- URL expiration is supported as a business rule target, even if some behavior is planned/future in the implementation.
- PostgreSQL is the system of record.

---

## 2. Test Strategy

| Test Type | Strategy | Coverage Goal |
|---|---|---|
| Functional Testing | Verify each API endpoint against expected business outcomes and response contracts. | 100% of documented APIs |
| Negative Testing | Validate error handling for malformed requests, wrong methods, and unknown resources. | All major invalid-input paths |
| Boundary Testing | Exercise minimum/maximum length, long identifiers, and edge-case values. | All documented limits |
| Validation Testing | Confirm field-level, format, and schema validation rules. | All request DTO rules |
| Security Testing | Assess OWASP API risks including injection, XSS, header abuse, and access control. | High-risk controls |
| Performance Testing | Measure latency, throughput, and stability under load. | SLA and concurrency targets |
| Concurrency Testing | Validate uniqueness, atomicity, and click-count consistency with parallel requests. | Race-condition hotspots |
| Regression Testing | Re-run core API scenarios after fixes or enhancements. | Critical path smoke set |
| Smoke Testing | Quick verification that APIs and persistence are operational. | Release gating |

### Approach Notes

- Test data will include valid production-like URLs, malformed URLs, encoded payloads, and large payloads.
- API checks will be complemented by repository/database assertions where persistence is involved.
- Security tests will be executed in a controlled environment only.
- Performance baselines will be measured for both single-request and concurrent-request scenarios.

---

## 3. Test Environment

| Component | Target Version / Tool | Purpose |
|---|---|---|
| Java | 21 | Application runtime |
| Spring Boot | 3.5.x | REST API framework |
| Database | PostgreSQL | Persistent URL storage |
| ORM | Spring Data JPA | Repository access |
| API Docs | Swagger / OpenAPI | Contract validation |
| API Client | Postman | Manual execution |
| Unit Testing | JUnit 5 | Automated tests |
| Mocking | Mockito | Service/controller isolation |
| Browser | Chrome / Edge | Swagger UI verification |
| Build Tool | Maven | Build and test execution |

### Environment Assumptions

- Test environment mirrors production configuration as closely as possible.
- Database schema migrations are applied before test execution.
- Logging and exception handling are enabled for defect triage.

---

## 4. Functional API Test Scenarios

### 4.1 POST `/api/urls/shorten`

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Validation Rules | Pass/Fail |
|---|---|---|---|---|---:|---|---|---|
| TST-POST-001 | Shorten a valid HTTPS URL | Positive | High | `{"originalUrl":"https://example.com/page"}` | 201 | `shortCode`, `originalUrl`, `createdDate`, `clickCount: 0` | URL is valid and stored | Pending |
| TST-POST-002 | Shorten a valid HTTP URL | Positive | High | `{"originalUrl":"http://example.com"}` | 201 | Resource created with short code | HTTP and HTTPS accepted | Pending |
| TST-POST-003 | Shorten URL with query parameters | Positive | Medium | `{"originalUrl":"https://example.com?a=1&b=2"}` | 201 | Query string preserved | URL integrity retained | Pending |
| TST-POST-004 | Create URL with future expiry date | Positive | Medium | `{"originalUrl":"https://example.com","expiryDate":"2026-12-31T23:59:59"}` | 201 | `expiryDate` returned | Expiry must be in future | Pending |
| TST-POST-005 | Generate unique short code on repeated submissions | Positive | High | Two identical create requests | 201 | Different `shortCode` values | Codes must be unique | Pending |
| TST-POST-006 | Create URL with internationalized path | Positive | Low | `{"originalUrl":"https://example.com/caf%C3%A9"}` | 201 | URL stored correctly | Encoded path preserved | Pending |

### 4.2 GET `/api/urls/{shortCode}` and GET `/api/urls/redirect/{shortCode}`

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Validation Rules | Pass/Fail |
|---|---|---|---|---|---:|---|---|---|
| TST-GET-001 | Retrieve URL details for known short code | Positive | High | `GET /api/urls/abc123` | 200 | URL details payload | Short code exists | Pending |
| TST-GET-002 | Redirect known short code to original URL | Positive | High | `GET /api/urls/redirect/abc123` | 302/200 | Redirect Location or original URL | Redirect target exists | Pending |
| TST-GET-003 | Retrieve analytics for known short code | Positive | High | `GET /api/urls/analytics/abc123` | 200 | `originalUrl`, `clickCount`, `createdAt`, `expiryDate` | Analytics record exists | Pending |
| TST-GET-004 | Unknown short code returns not found | Negative | High | `GET /api/urls/unknown1` | 404 | Resource not found message | No matching record | Pending |
| TST-GET-005 | Expired short code returns gone | Negative | High | `GET /api/urls/abc123` (expired) | 410 | Expired resource message | Expiry time passed | Pending |
| TST-GET-006 | Redirect increments click count atomically | Positive | High | Repeated redirect calls | 200/302 | Updated click count | Count increases by 1 per success | Pending |

### 4.3 PUT `/api/urls/{id}` and DELETE `/api/urls/{id}`

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Validation Rules | Pass/Fail |
|---|---|---|---|---|---:|---|---|---|
| TST-PUT-001 | Update original URL for existing record | Positive | High | `PUT /api/urls/1` with valid body | 200 | Updated URL details | ID exists and body valid | Pending |
| TST-PUT-002 | Update expiry date for existing record | Positive | Medium | `PUT /api/urls/1` with future expiry | 200 | Updated expiry date | Expiry must remain future-dated | Pending |
| TST-PUT-003 | Update unknown ID | Negative | High | `PUT /api/urls/9999` | 404 | Resource not found message | ID does not exist | Pending |
| TST-DEL-001 | Delete existing record | Positive | High | `DELETE /api/urls/1` | 204 | Empty body | Record removed from database | Pending |
| TST-DEL-002 | Delete unknown record | Negative | High | `DELETE /api/urls/9999` | 404 | Resource not found message | ID does not exist | Pending |
| TST-DEL-003 | Delete already deleted record | Negative | Medium | Repeat delete on same ID | 404 | Not found message | Idempotent cleanup verified | Pending |

---

## 5. Negative Test Scenarios

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Pass/Fail |
|---|---|---|---|---|---:|---|---|
| TST-NEG-001 | Invalid HTTP method on create endpoint | Negative | High | `GET /api/urls/shorten` | 405 | Method not allowed | Pending |
| TST-NEG-002 | Missing `Content-Type` header | Negative | High | POST without header | 400 | Validation error | Pending |
| TST-NEG-003 | Unsupported content type | Negative | High | `Content-Type: text/plain` | 415/400 | Unsupported media type | Pending |
| TST-NEG-004 | Malformed JSON body | Negative | High | Broken JSON payload | 400 | Parse error response | Pending |
| TST-NEG-005 | Unknown endpoint | Negative | Medium | `GET /api/unknown` | 404 | Not found | Pending |
| TST-NEG-006 | Missing request body | Negative | High | Empty POST body | 400 | Required field error | Pending |
| TST-NEG-007 | Unsupported protocol | Negative | High | `ftp://example.com` | 400 | Invalid URL format | Pending |
| TST-NEG-008 | Expired resource access | Negative | High | Access expired short code | 410 | Gone response | Pending |
| TST-NEG-009 | Invalid path variable format | Negative | Medium | `GET /api/urls/###` | 400/404 | Invalid identifier handling | Pending |
| TST-NEG-010 | Duplicate submission with system conflict | Negative | Medium | Same payload under simulated duplicate race | 409/201 | Conflict or unique creation | Pending |

---

## 6. Boundary Test Cases

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Pass/Fail |
|---|---|---|---|---|---:|---|---|
| TST-BND-001 | Minimum valid URL length | Boundary | Medium | `https://a.co` | 201 | Short code returned | Pending |
| TST-BND-002 | Maximum allowed URL length | Boundary | High | 2048-char URL | 201 | URL accepted and stored | Pending |
| TST-BND-003 | URL length over maximum by 1 char | Boundary | High | 2049-char URL | 400 | Length validation error | Pending |
| TST-BND-004 | Minimum expiry offset | Boundary | Low | Expiry set to nearest allowed future time | 201 | Accepted | Pending |
| TST-BND-005 | Very long query string | Boundary | Medium | URL with large query component | 201 | Query preserved | Pending |
| TST-BND-006 | Maximum practical short code length | Boundary | Medium | Path variable longer than expected | 404/400 | Invalid code handling | Pending |

---

## 7. Validation Test Cases

| Test ID | Scenario | Category | Priority | Request | Expected Status Code | Expected Response | Pass/Fail |
|---|---|---|---|---|---:|---|---|
| TST-VAL-001 | Missing `originalUrl` field | Validation | High | `{}` | 400 | Field required message | Pending |
| TST-VAL-002 | Null `originalUrl` | Validation | High | `{"originalUrl":null}` | 400 | Null not allowed | Pending |
| TST-VAL-003 | Empty string `originalUrl` | Validation | High | `{"originalUrl":""}` | 400 | Blank value rejected | Pending |
| TST-VAL-004 | Whitespace-only URL | Validation | High | `{"originalUrl":"   "}` | 400 | Blank value rejected | Pending |
| TST-VAL-005 | Invalid URL format | Validation | High | `{"originalUrl":"example.com"}` | 400 | Protocol/format error | Pending |
| TST-VAL-006 | Invalid JSON syntax | Validation | High | Malformed JSON | 400 | JSON parse error | Pending |
| TST-VAL-007 | Large payload in request body | Validation | Medium | Oversized JSON | 413/400 | Payload rejected | Pending |
| TST-VAL-008 | Special characters in URL | Validation | Medium | URL with encoded symbols | 201 | Preserved input | Pending |
| TST-VAL-009 | Unicode URL | Validation | Medium | `https://example.com/こんにちは` | 201 | Unicode accepted or encoded | Pending |
| TST-VAL-010 | Missing path variable on GET | Validation | Medium | `GET /api/urls/` | 404 | Endpoint routing error | Pending |

---

## 8. Security Test Cases (OWASP API)

| Test ID | Attack Type | Payload Example | Expected Response | Mitigation | Pass/Fail |
|---|---|---|---|---|---|
| TST-SEC-001 | SQL Injection | `https://x.com' OR '1'='1` | Rejected or safely stored | Parameterized persistence | Pending |
| TST-SEC-002 | Cross Site Scripting | `<script>alert(1)</script>` in URL | No script execution | Output encoding / validation | Pending |
| TST-SEC-003 | Script Injection | `<img src=x onerror=alert(1)>` | No execution | Input sanitization | Pending |
| TST-SEC-004 | Command Injection | `; rm -rf /` | No command execution | No shell execution path | Pending |
| TST-SEC-005 | Header Injection | `\r\nX-Test:1` | Rejected or normalized | Header sanitization | Pending |
| TST-SEC-006 | Large Payload Attack | Oversized body | 413/400 | Request size limiting | Pending |
| TST-SEC-007 | Open Redirect Abuse | Malicious external URL | Stored only if allowed by policy | Redirect allowlist / warning | Pending |
| TST-SEC-008 | Brute Force Enumeration | Many short-code guesses | 404 with no data leakage | Rate limiting / monitoring | Pending |
| TST-SEC-009 | Sensitive Data Exposure | Error-triggering payload | No stack trace or secrets | Secure error handling | Pending |
| TST-SEC-010 | Authentication / Authorization | Missing auth on protected action | 401/403 where enforced | AuthN/AuthZ controls | Pending |

---

## 9. Performance Test Strategy

| Metric | Target | Measurement Method | Acceptance |
|---|---:|---|---|
| URL creation latency | < 500 ms | Median and 95th percentile | Pass when sustained |
| Redirect latency | < 200 ms | Median and 95th percentile | Pass when sustained |
| Concurrent users | 10 / 50 / 100 / 500 | Load tool execution | No functional degradation |
| Stress behavior | Beyond expected peak | Increase ramp until failure | Fail gracefully, no corruption |
| Spike behavior | Sudden 10x burst | Burst simulation | Stable recovery |
| CPU usage | Acceptable under load | OS/application metrics | No sustained saturation |
| Memory usage | Stable, no leak | Heap and process metrics | No leak pattern |

### Performance Execution Plan

1. Establish baseline using single-user create and redirect calls.
2. Run load profiles at target concurrency levels.
3. Apply stress and spike patterns to verify resilience.
4. Observe database connection usage and GC behavior.
5. Capture latency, throughput, CPU, and memory metrics.

---

## 10. Database Validation

| Test ID | Scenario | Expected Database Result | Priority | Pass/Fail |
|---|---|---|---|---|
| TST-DB-001 | Create URL inserts row | One row created in URL table | High | Pending |
| TST-DB-002 | Duplicate short code prevention | No duplicate short code rows | High | Pending |
| TST-DB-003 | Redirect updates click count | `clickCount` incremented atomically | High | Pending |
| TST-DB-004 | Update persists changes | Modified fields stored correctly | High | Pending |
| TST-DB-005 | Delete removes row | Record no longer exists | High | Pending |
| TST-DB-006 | Failed transaction rollback | No partial write on error | Medium | Pending |
| TST-DB-007 | Referential integrity | No orphan analytics/persistence records | Medium | Pending |

---

## 11. Unit Testing Strategy

| Layer | Focus | Tools | Coverage Goal |
|---|---|---|---|
| Service Layer | Business rules, uniqueness, expiry, click tracking | JUnit 5, Mockito | > 80% |
| Controller Layer | Request mapping, status codes, validation | MockMvc / JUnit 5 | > 80% |
| Repository Layer | Query correctness and persistence behavior | Spring Data JPA tests | Critical queries |
| Validation Layer | URL format and field constraints | Bean Validation tests | All constraints |
| Exception Handling | Error mapping and response payloads | JUnit 5, Mockito | All major errors |

### Strategy Notes

- Service tests will mock repositories and verify business rules.
- Controller tests will isolate HTTP behavior and error handling.
- Exception tests will ensure consistent API error messages.

---

## 12. Integration Testing Strategy

| Test Scope | Validation Focus | Expected Outcome |
|---|---|---|
| API + Database | End-to-end persistence and retrieval | Data stored and read correctly |
| Repository + Database | Query execution and mapping | Correct entity state |
| Controller + Service | Full request/response flow | Correct status and payload |
| Redirect Flow | Lookup, redirect, click update | Click count increments safely |

### Integration Approach

- Execute against a real PostgreSQL instance in a dedicated test environment.
- Validate both HTTP response and database state after each transaction.
- Include end-to-end scenarios for create, redirect, analytics, update, and delete.

---

## 13. Test Data

| Data Set | Example | Purpose |
|---|---|---|
| Valid URL | `https://example.com` | Happy-path create/update |
| Valid URL with query | `https://example.com?a=1&b=2` | Query preservation |
| Expiring URL | `https://example.com` with future expiry | Expiry validation |
| Invalid URL | `example.com` | Format rejection |
| Oversized URL | 2049 characters | Boundary validation |
| Malicious payload | `<script>alert(1)</script>` | Security testing |
| Unicode URL | `https://example.com/こんにちは` | Encoding support |
| Concurrent data | 100 requests for same base URL | Uniqueness and race checks |

---

## 14. Entry & Exit Criteria

| Type | Criteria |
|---|---|
| Entry | Requirements reviewed, build succeeds, schema deployed, test data prepared, environment stable |
| Entry | Swagger/OpenAPI available and API base URL reachable |
| Exit | All High-priority tests passed |
| Exit | No open critical or high-severity defects |
| Exit | Database integrity verified |
| Exit | Performance meets SLA targets |
| Exit | Security checks completed for critical attack cases |

---

## 15. Deliverables

| Deliverable | Description |
|---|---|
| API Test Plan | This document |
| Postman Collection | Executable request set for APIs |
| JUnit Tests | Automated unit and integration tests |
| Coverage Report | Code coverage summary |
| Test Execution Report | Results, defects, and sign-off evidence |

---

## 16. Requirements Traceability Matrix

| Requirement | API / Area | Test Case | Status |
|---|---|---|---|
| Create Short URL | `POST /api/urls/shorten` | TST-POST-001, TST-POST-002 | Planned |
| Store URL Mapping | Persistence layer | TST-DB-001, TST-DB-004 | Planned |
| Generate Unique Short Code | `POST /api/urls/shorten` | TST-POST-005, TST-DB-002 | Planned |
| Validate URLs | Create / update APIs | TST-VAL-001 to TST-VAL-009 | Planned |
| Handle Invalid Request | All APIs | TST-NEG-001 to TST-NEG-010, TST-VAL-001 to TST-VAL-010 | Planned |
| Track Click Count | `GET /api/urls/redirect/{shortCode}` | TST-GET-006, TST-DB-003 | Planned |
| URL Expiry | Create / retrieve / analytics / update flows | TST-POST-004, TST-GET-005, TST-GET-003, TST-PUT-002 | Planned |
| URL shortening within 500 ms | Performance testing | Performance Test Strategy | Planned |
| URL redirection within 200 ms | Performance testing | Performance Test Strategy | Planned |
| APIs responsive under concurrent requests | Concurrency / performance | Performance Test Strategy | Planned |
| Scalability (millions, stateless, horizontal scale) | Architecture / performance | Test Strategy, Performance Test Strategy | Planned |
| Security (validation, sanitization, injection prevention) | All APIs | TST-SEC-001 to TST-SEC-010 | Planned |
| Maintainability (SOLID, clean design) | Code structure | Unit Testing Strategy, Integration Testing Strategy | Planned |
| Testability (unit, integration, mocking) | Test environment and strategy | TST-VAL-001 to TST-VAL-010, unit/integration sections | Planned |
| Observability (logging, exceptions, health, metrics) | Runtime behavior | API documentation and error handling | Planned |

---

## 17. AI-Assisted Testing Approach

AI was used to accelerate test planning, scenario generation, and documentation drafting. The AI produced the initial artifacts for this test plan, including the scenario structure, negative-path ideas, and coverage categories. Human QA engineering review remained mandatory for every test case and every assumption.

### AI Usage Areas

- Test planning
- Scenario generation
- JUnit design support
- Postman collection drafting
- Failure analysis support

### Human Ownership Statement

- AI generated the initial artifacts.
- Human engineer reviewed every test.
- Human engineer executed every test.
- Human engineer approved all results.
- Engineer retained full ownership of correctness and production readiness.

### Governance

AI output was treated as a starting point, not as authoritative truth. Final test scope, risk acceptance, and production readiness decisions remained under human control.