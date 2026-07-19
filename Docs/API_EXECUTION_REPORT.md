# API Execution Report — URL Shortener

**Role**: Senior QA Engineer  
**Date**: 2026-07-19  
**Environment**: `http://localhost:8080`  
**Application**: Spring Boot URL Shortener (Java 21, H2 in-memory DB)  
**Execution method**: Live `curl` against running application

---

## Endpoints Discovered

| # | Method | Path | Description |
|---|---|---|---|
| 1 | POST | `/api/urls/shorten` | Create a short URL |
| 2 | GET | `/api/urls/{shortCode}` | Get URL details by short code |
| 3 | GET | `/api/urls/redirect/{shortCode}` | Redirect — return original URL + increment click count |
| 4 | GET | `/api/urls/analytics/{shortCode}` | Get analytics without incrementing click count |
| 5 | PUT | `/api/urls/{id}` | Update original URL |
| 6 | DELETE | `/api/urls/{id}` | Delete URL record |

---

## Overall Summary

| Category | Count |
|---|---|
| Total test cases executed | 36 |
| **PASS** | **28** |
| **FAIL** | **8** |
| Defects found | 4 |

---

## Section 1 — POST `/api/urls/shorten`

### TC-POST-01 · Positive · Valid HTTPS URL

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"https://github.com/parmanandmishra/url-shortener-ai"}` |
| **Response Status** | `201 Created` |
| **Response Body** | `{"id":1,"originalUrl":"https://github.com/...","shortCode":"SVfUhA","createdDate":"2026-07-19T18:30:16","expiryDate":null,"clickCount":0}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Valid URL accepted, unique 6-char short code generated, clickCount initialised to 0 |

---

### TC-POST-02 · Positive · Auto-Normalize Domain Without Protocol

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"google.com"}` |
| **Response Status** | `201 Created` |
| **Response Body** | `{"id":2,"originalUrl":"https://google.com","shortCode":"leShXc",...}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | `google.com` auto-normalized to `https://google.com` — stored URL is protocol-complete |

---

### TC-POST-03 · Invalid Request · Unsupported Protocol

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"ftp://example.com/file"}` |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL must start with http:// or https://","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Explicit unsupported protocol correctly rejected with clean user-facing message |

---

### TC-POST-04 · Boundary · URL Exactly 2048 Characters (Maximum)

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"https://example.com/aaa..."}` (2048 chars) |
| **Response Status** | `201 Created` |
| **Response Body** | Short code returned, full URL stored |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Maximum allowed length accepted |

---

### TC-POST-05 · Boundary · URL 2049 Characters (One Over Maximum)

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"https://example.com/aaa..."}` (2049 chars) |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL must be between 10 and 2048 characters","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Over-limit URL correctly rejected by Bean Validation |

---

### TC-POST-06 · Missing Field · Empty JSON Body

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{}` |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL cannot be blank","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Missing required field triggers `@NotBlank` constraint |

---

### TC-POST-07 · Missing Field · Null originalUrl

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":null}` |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL cannot be blank","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Null value rejected by Bean Validation |

---

### TC-POST-08 · Invalid JSON · Unquoted Key

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{originalUrl:"https://example.com"}` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: JSON parse error: Unexpected character ('o'..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-001** — Malformed JSON should return `400 Bad Request`. `HttpMessageNotReadableException` is not handled in `GlobalExceptionHandler`, so it falls through to the generic 500 handler. Internal Jackson parse details exposed in error message. |

---

### TC-POST-09 · Invalid JSON · Truncated Payload

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/shorten` |
| **Request** | `{"originalUrl":"https://example.com"` (no closing brace) |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: JSON parse error: Unexpected end-of-input..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-001** (same root cause) — Incomplete JSON should return `400`. Same `HttpMessageNotReadableException` not mapped. |

---

### TC-POST-10 · Resource Not Found · Wrong HTTP Method (GET on POST endpoint)

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/shorten` |
| **Request** | No body |
| **Response Status** | `404 Not Found` ⚠️ |
| **Response Body** | `{"status":404,"message":"Short URL not found: shorten","timestamp":"..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-002** — Expected `405 Method Not Allowed`. `GET /api/urls/shorten` is routed to `GET /api/urls/{shortCode}` treating "shorten" as a short code, so it returns 404 instead of 405. Route path collision. |

---

## Section 2 — GET `/api/urls/{shortCode}`

### TC-GET-01 · Positive · Valid Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/SVfUhA` |
| **Response Status** | `200 OK` |
| **Response Body** | `{"id":1,"originalUrl":"https://github.com/...","shortCode":"SVfUhA","createdDate":"...","expiryDate":null,"clickCount":0}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Correct record returned, all fields present |

---

### TC-GET-02 · Invalid Request · XSS Attempt in Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/%3Cscript%3E` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: <script>","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Script tag treated as unknown short code. No code execution. 404 returned safely. |

---

### TC-GET-03 · Boundary · 50-Character Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: aaa...","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Non-matching long short code handled gracefully |

---

### TC-GET-04 · Resource Not Found · Non-Existent Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/XXXXXX` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: XXXXXX","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Unknown short code returns 404 with clear message |

---

### TC-GET-05 · Invalid · Completely Unknown Path

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/unknownpath` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: No static resource api/unknownpath.","timestamp":"..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-003** — Unknown paths should return `404`. Spring's `NoResourceFoundException` is not mapped in `GlobalExceptionHandler`, exposing internal detail and returning 500 instead of 404. |

---

## Section 3 — GET `/api/urls/redirect/{shortCode}`

### TC-REDIR-01 · Positive · Redirect Returns Original URL

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/redirect/SVfUhA` |
| **Response Status** | `200 OK` |
| **Response Body** | `https://github.com/parmanandmishra/url-shortener-ai` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Original URL returned; click count incremented in background |

---

### TC-REDIR-02 · Positive · Click Count Increments on Redirect

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/redirect/SVfUhA` (second call) |
| **Response Status** | `200 OK` |
| **Response Body** | `https://github.com/parmanandmishra/url-shortener-ai` |
| **Verification** | Analytics confirmed `clickCount: 2` after two redirects |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Click count correctly incremented per redirect call |

---

### TC-REDIR-03 · Negative · Non-Existent Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/redirect/NOTEXIST` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: NOTEXIST","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Unknown short code returns 404 |

---

### TC-REDIR-04 · Boundary · 1-Character Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/redirect/a` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: a","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Single-char short code handled gracefully |

---

### TC-REDIR-05 · Invalid · Wrong Method POST on Redirect

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/redirect/SVfUhA` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: Request method 'POST' is not supported","timestamp":"..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-004** — Wrong HTTP method should return `405 Method Not Allowed`. `HttpRequestMethodNotSupportedException` not mapped in `GlobalExceptionHandler`. |

---

## Section 4 — GET `/api/urls/analytics/{shortCode}`

### TC-ANA-01 · Positive · Analytics With Accurate Click Count

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/analytics/SVfUhA` |
| **Response Status** | `200 OK` |
| **Response Body** | `{"originalUrl":"https://github.com/...","shortCode":"SVfUhA","clickCount":2,"createdDate":"2026-07-19T18:30:16","expiryDate":null}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | `clickCount: 2` matches the two redirect calls made earlier. All fields present. |

---

### TC-ANA-02 · Positive · Analytics Does Not Increment Click Count

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/analytics/SVfUhA` (two consecutive calls) |
| **Response Status** | `200 OK` |
| **Response Body** | `clickCount: 2` on both calls — unchanged |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Analytics is read-only — does not increment click count |

---

### TC-ANA-03 · Boundary · 1-Character Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/analytics/z` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: z","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Short code not found returns 404 |

---

### TC-ANA-04 · Resource Not Found · Non-Existent Short Code

| Field | Value |
|---|---|
| **Endpoint** | `GET /api/urls/analytics/XXXXXX` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"Short URL not found: XXXXXX","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Unknown short code returns 404 |

---

### TC-ANA-05 · Invalid · Wrong Method POST on Analytics

| Field | Value |
|---|---|
| **Endpoint** | `POST /api/urls/analytics/SVfUhA` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: Request method 'POST' is not supported","timestamp":"..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-004** (same root cause) — Should return `405 Method Not Allowed`. |

---

## Section 5 — PUT `/api/urls/{id}`

### TC-PUT-01 · Positive · Update URL

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/1` |
| **Request** | `{"originalUrl":"https://www.updated-example.com/new-page"}` |
| **Response Status** | `200 OK` |
| **Response Body** | `{"id":1,"originalUrl":"https://www.updated-example.com/new-page","shortCode":"SVfUhA","createdDate":"...","expiryDate":null,"clickCount":2}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Original URL updated; short code and click count preserved |

---

### TC-PUT-02 · Negative · Invalid Protocol

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/1` |
| **Request** | `{"originalUrl":"ftp://bad-protocol.com"}` |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL must start with http:// or https://","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Invalid protocol correctly rejected |

---

### TC-PUT-03 · Boundary · URL Exactly 2048 Characters

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/1` |
| **Request** | `{"originalUrl":"https://example.com/bbb..."}` (2048 chars) |
| **Response Status** | `200 OK` |
| **Response Body** | Short code returned |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Maximum length URL accepted on update |

---

### TC-PUT-04 · Missing Field · Empty Body

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/1` |
| **Request** | `{}` |
| **Response Status** | `400 Bad Request` |
| **Response Body** | `{"status":400,"message":"URL cannot be blank","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Missing required field rejected |

---

### TC-PUT-05 · Invalid JSON · Truncated Payload

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/1` |
| **Request** | `{"originalUrl":"https://example.com"` (truncated) |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: JSON parse error...","timestamp":"..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-001** (same root cause) — Malformed JSON should return `400`. |

---

### TC-PUT-06 · Resource Not Found · Non-Existent ID

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/9999` |
| **Request** | `{"originalUrl":"https://example.com"}` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"URL not found with id: 9999","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Non-existent ID returns 404 with clear message |

---

### TC-PUT-07 · Invalid · String ID Path Variable

| Field | Value |
|---|---|
| **Endpoint** | `PUT /api/urls/abc` |
| **Request** | `{"originalUrl":"https://example.com"}` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-001** (same family) — Type mismatch on path variable should return `400 Bad Request`. `MethodArgumentTypeMismatchException` not mapped. Internal type detail exposed. |

---

## Section 6 — DELETE `/api/urls/{id}`

### TC-DEL-01 · Positive · Delete Existing Record

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/4` |
| **Response Status** | `204 No Content` |
| **Response Body** | (empty) |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Record deleted; empty body and 204 returned as specified |

---

### TC-DEL-02 · Negative · Delete Already-Deleted Record

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/4` (second call) |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"URL not found with id: 4","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Idempotent — once deleted, subsequent deletes return 404 |

---

### TC-DEL-03 · Boundary · ID = 0

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/0` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"URL not found with id: 0","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | ID 0 does not exist; returns 404 gracefully |

---

### TC-DEL-04 · Boundary · Negative ID

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/-1` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"URL not found with id: -1","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Negative ID handled gracefully |

---

### TC-DEL-05 · Resource Not Found · Large Non-Existent ID

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/9999` |
| **Response Status** | `404 Not Found` |
| **Response Body** | `{"status":404,"message":"URL not found with id: 9999","timestamp":"..."}` |
| **Pass/Fail** | ✅ PASS |
| **Reason** | Non-existent ID returns 404 |

---

### TC-DEL-06 · Invalid · String ID Path Variable

| Field | Value |
|---|---|
| **Endpoint** | `DELETE /api/urls/notanid` |
| **Response Status** | `500 Internal Server Error` ⚠️ |
| **Response Body** | `{"status":500,"message":"An unexpected error occurred: Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'..."}` |
| **Pass/Fail** | ❌ FAIL |
| **Reason** | **DEFECT-001** — Non-numeric ID should return `400 Bad Request`. Same `MethodArgumentTypeMismatchException` not mapped. |

---

## Defects Found

### DEFECT-001 — Invalid/Malformed Input Returns HTTP 500 Instead of 400

| Field | Detail |
|---|---|
| **Severity** | High |
| **Affected endpoints** | POST `/shorten`, PUT `/{id}`, DELETE `/{id}` |
| **Affected test cases** | TC-POST-08, TC-POST-09, TC-PUT-05, TC-PUT-07, TC-DEL-06 |
| **Root cause** | `GlobalExceptionHandler` does not handle `HttpMessageNotReadableException` (malformed JSON) or `MethodArgumentTypeMismatchException` (wrong path variable type). Both fall through to the generic `Exception` handler which returns 500. |
| **Fix** | Add `@ExceptionHandler` for `HttpMessageNotReadableException` → `400`, and `MethodArgumentTypeMismatchException` → `400` in `GlobalExceptionHandler`. |

---

### DEFECT-002 — Route Collision: `GET /api/urls/shorten` Returns 404 Instead of 405

| Field | Detail |
|---|---|
| **Severity** | Medium |
| **Affected test case** | TC-POST-10 |
| **Root cause** | `GET /api/urls/{shortCode}` captures any path segment including "shorten", so `GET /api/urls/shorten` resolves to a short code lookup rather than returning `405 Method Not Allowed`. |
| **Fix** | Use `/api/urls/code/{shortCode}` for the short code lookup, or add a specific `GET /api/urls/shorten` handler returning 405. |

---

### DEFECT-003 — Unknown Path Returns HTTP 500 Instead of 404

| Field | Detail |
|---|---|
| **Severity** | Medium |
| **Affected test case** | TC-GET-05 |
| **Root cause** | `NoResourceFoundException` (Spring 6+) is not handled in `GlobalExceptionHandler`. Falls through to the generic 500 handler. |
| **Fix** | Add `@ExceptionHandler(NoResourceFoundException.class)` → `404` in `GlobalExceptionHandler`. |

---

### DEFECT-004 — Wrong HTTP Method Returns HTTP 500 Instead of 405

| Field | Detail |
|---|---|
| **Severity** | Medium |
| **Affected test cases** | TC-REDIR-05, TC-ANA-05 |
| **Root cause** | `HttpRequestMethodNotSupportedException` is not handled in `GlobalExceptionHandler`. Falls through to the generic 500 handler. |
| **Fix** | Add `@ExceptionHandler(HttpRequestMethodNotSupportedException.class)` → `405` in `GlobalExceptionHandler`. |

---

## Pass/Fail Summary by Endpoint

| Endpoint | Total | Pass | Fail |
|---|---|---|---|
| `POST /api/urls/shorten` | 10 | 7 | 3 |
| `GET /api/urls/{shortCode}` | 5 | 4 | 1 |
| `GET /api/urls/redirect/{shortCode}` | 5 | 4 | 1 |
| `GET /api/urls/analytics/{shortCode}` | 5 | 4 | 1 |
| `PUT /api/urls/{id}` | 7 | 5 | 2 |
| `DELETE /api/urls/{id}` | 6 | 5 | 1 |
| **TOTAL** | **38** | **29** | **9** |

---

## Observations

| # | Observation |
|---|---|
| 1 | `google.com` is now accepted and auto-normalized to `https://google.com` — good UX improvement. |
| 2 | Analytics endpoint correctly does **not** increment click count — verified by comparing click count before and after analytics calls. |
| 3 | Short code uniqueness validated — two requests with identical input produced different short codes. |
| 4 | `expiryDate: null` correctly returned when no expiry is set. |
| 5 | Internal Jackson parse error messages are still partially exposed in the 500 responses (Defect-001). |
| 6 | `DELETE /api/urls/-1` and `DELETE /api/urls/0` return 404 gracefully — no unhandled edge case. |
| 7 | Swagger/OpenAPI available at `http://localhost:8080/swagger-ui.html` for interactive testing. |

---

## AI Engineering Note

Test scenarios were generated with AI assistance. All test cases were executed live against the running application by the engineer. All Pass/Fail results were reviewed and validated by the engineer. The engineer retains full ownership of this report and its findings.

---

*Report generated: 2026-07-19 · Application: URL Shortener · Base URL: http://localhost:8080*
