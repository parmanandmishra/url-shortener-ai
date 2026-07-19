# API Test Scenarios - POST /api/url/shorten

**Endpoint**: `POST /api/url/shorten`  
**Expected HTTP Status**: `201 Created`  
**Content-Type**: `application/json`

---

## 1. POSITIVE TESTS

### Tests for Valid Inputs and Expected Behaviors

| Test ID | Test Case | Request Body | Expected Response | Status Code | Assertion |
|---------|-----------|--------------|------------------|-------------|-----------|
| POS-001 | Create URL with valid HTTPS | `{"originalUrl":"https://www.example.com/page"}` | Contains `shortCode`, `originalUrl`, `id`, `createdDate`, `clickCount: 0` | 201 | Short code generated, URL stored |
| POS-002 | Create URL with valid HTTP | `{"originalUrl":"http://example.com"}` | Contains `shortCode`, `originalUrl`, `id`, `createdDate` | 201 | Both HTTP and HTTPS supported |
| POS-003 | Create URL with query parameters | `{"originalUrl":"https://example.com/page?ref=abc&id=123"}` | Contains preserved `originalUrl` with query params | 201 | Query parameters intact |
| POS-004 | Create URL with fragments | `{"originalUrl":"https://example.com/page#section"}` | Contains preserved `originalUrl` with fragment | 201 | URL fragments preserved |
| POS-005 | Create URL with port number | `{"originalUrl":"https://example.com:8080/secure/path"}` | Contains `shortCode`, `originalUrl` with port | 201 | Port numbers supported |
| POS-006 | Create URL with special characters | `{"originalUrl":"https://example.com/api?key=abc-123_456.txt"}` | URL stored with special characters | 201 | Special characters preserved |
| POS-007 | Create URL with long path | `{"originalUrl":"https://example.com/very/long/nested/path/structure/to/resource"}` | Contains `shortCode`, full path preserved | 201 | Long paths supported |
| POS-008 | Create URL with expiry date (future) | `{"originalUrl":"https://example.com","expiryDate":"2026-12-31T23:59:59"}` | Response contains `expiryDate: 2026-12-31T23:59:59` | 201 | Expiry date stored correctly |
| POS-009 | Create URL without expiry date | `{"originalUrl":"https://example.com"}` | Response contains `expiryDate: null` | 201 | Optional expiry date handled |
| POS-010 | Verify short code uniqueness | Create 2 URLs with same originalUrl | Both have different `shortCode` values | 201 | Unique codes generated each time |
| POS-011 | Verify short code format | `{"originalUrl":"https://example.com"}` | `shortCode` matches regex `^[a-zA-Z0-9]{6}$` | 201 | 6-character alphanumeric code |
| POS-012 | Response includes all required fields | `{"originalUrl":"https://example.com"}` | Response has id, originalUrl, shortCode, createdDate, clickCount, expiryDate | 201 | All mandatory fields present |
| POS-013 | Create URL with subdomain | `{"originalUrl":"https://api.example.com/endpoint"}` | Contains `shortCode`, subdomain preserved | 201 | Subdomains supported |
| POS-014 | Create URL with international domain | `{"originalUrl":"https://例え.jp/page"}` | URL stored with international characters | 201 | IDN domains supported |
| POS-015 | Click count initialized to zero | `{"originalUrl":"https://example.com"}` | Response contains `clickCount: 0` | 201 | Click count starts at 0 |

---

## 2. NEGATIVE TESTS

### Tests for Invalid Inputs and Error Handling

| Test ID | Test Case | Request Body | Expected Response | Status Code | Assertion |
|---------|-----------|--------------|------------------|-------------|-----------|
| NEG-001 | Missing originalUrl field | `{}` | Error message: "URL cannot be blank" or "originalUrl is required" | 400 | Required field validation |
| NEG-002 | Null originalUrl value | `{"originalUrl":null}` | Error message about null/blank URL | 400 | Null value rejected |
| NEG-003 | Empty string originalUrl | `{"originalUrl":""}` | Error message: "URL cannot be blank" | 400 | Empty string rejected |
| NEG-004 | URL without protocol | `{"originalUrl":"example.com"}` | Error message: "Invalid URL format: no protocol specified" | 400 | Protocol validation |
| NEG-005 | Invalid protocol (FTP) | `{"originalUrl":"ftp://example.com"}` | Error message: "Unsupported protocol" or "only http/https" | 400 | Protocol whitelist enforced |
| NEG-006 | Invalid protocol (file) | `{"originalUrl":"file:///etc/passwd"}` | Error message about unsupported protocol | 400 | File protocol blocked |
| NEG-007 | Incomplete URL (protocol only) | `{"originalUrl":"https://"}` | Error message: "Invalid URL format" | 400 | URL completeness validated |
| NEG-008 | Malformed URL (spaces) | `{"originalUrl":"https://exam ple.com"}` | Error message: "Invalid URL format" | 400 | Spaces rejected in URL |
| NEG-009 | URL with only whitespace | `{"originalUrl":"   "}` | Error message: "URL cannot be blank" | 400 | Whitespace-only rejected |
| NEG-010 | Invalid JSON format | `{originalUrl: "https://example.com"}` | Error message: "Invalid JSON" | 400 | JSON parsing validation |
| NEG-011 | Missing closing brace | `{"originalUrl":"https://example.com"` | Error message: "Invalid JSON" | 400 | JSON syntax validation |
| NEG-012 | Double quotes missing | `{'originalUrl':'https://example.com'}` | Error message: "Invalid JSON" | 400 | JSON format enforcement |
| NEG-013 | Invalid expiryDate format | `{"originalUrl":"https://example.com","expiryDate":"2026/12/31"}` | Error message: "Invalid date format" | 400 | ISO 8601 format enforced |
| NEG-014 | Expiry date in past | `{"originalUrl":"https://example.com","expiryDate":"2020-12-31T23:59:59"}` | Error message: "Expiry date must be in future" | 400 | Past dates rejected |
| NEG-015 | Expiry date (current time) | `{"originalUrl":"https://example.com","expiryDate":"2026-07-18T21:04:00"}` | Error message: "Expiry date must be in future" | 400 | Present time rejected |
| NEG-016 | Additional unknown fields | `{"originalUrl":"https://example.com","customField":"value"}` | Either accepted (ignored) or rejected with error | 400 or 201 | Unknown field handling |
| NEG-017 | Negative or zero expiryDate | `{"originalUrl":"https://example.com","expiryDate":0}` | Error message: "Invalid date format" | 400 | Invalid date type |
| NEG-018 | SQL injection attempt | `{"originalUrl":"https://example.com' OR '1'='1"}` | URL stored as-is or escaped properly | 201 | SQL injection prevented |
| NEG-019 | HTML injection attempt | `{"originalUrl":"https://example.com<script>alert('xss')</script>"}` | URL stored as-is, no code execution | 201 | HTML injection prevented |

---

## 3. BOUNDARY TESTS

### Tests for Edge Cases and Limits

| Test ID | Test Case | Request Body | Expected Response | Status Code | Assertion |
|---------|-----------|--------------|------------------|-------------|-----------|
| BND-001 | Minimum valid URL length (10 chars) | `{"originalUrl":"https://a.c"}` | Contains `shortCode` (11 chars) | 201 | Min length URL accepted |
| BND-002 | Maximum valid URL length (2048 chars) | URL with 2048 characters | Contains `shortCode` | 201 | Max length URL accepted |
| BND-003 | URL exceeding max length (2049 chars) | URL with 2049 characters | Error message: "URL exceeds maximum length of 2048" | 400 | Over-limit rejected |
| BND-004 | Very long valid URL (1900 chars) | 1900 character valid URL | Contains `shortCode`, URL preserved | 201 | Large URLs handled |
| BND-005 | URL with maximum query string | URL with 1000+ character query string | Contains `shortCode`, query preserved | 201 | Long query strings supported |
| BND-006 | Very short domain (2 chars) | `{"originalUrl":"https://ab.c"}` | Contains `shortCode` | 201 | Short domains accepted |
| BND-007 | Very long domain (63+ chars per label) | `{"originalUrl":"https://verylongsubdomainname.verylongdomainname.example.com"}` | Contains `shortCode` | 201 | Long domain names supported |
| BND-008 | URL with 100+ query parameters | URL with many query params | Contains `shortCode`, all params preserved | 201 | High parameter count handled |
| BND-009 | Zero-length expiryDate string | `{"originalUrl":"https://example.com","expiryDate":""}` | Error message: "Invalid date format" | 400 | Empty date string rejected |
| BND-010 | Maximum future expiry (10 years) | `{"originalUrl":"https://example.com","expiryDate":"2036-07-18T21:04:00"}` | Contains `expiryDate: 2036-07-18T21:04:00` | 201 | Far future dates supported |
| BND-011 | Expiry beyond 10 years (11 years) | `{"originalUrl":"https://example.com","expiryDate":"2037-07-18T21:04:00"}` | May accept or reject based on business rules | 201 or 400 | Expiry limit enforcement |
| BND-012 | Minimum expiry (1 minute future) | `{"originalUrl":"https://example.com","expiryDate":"2026-07-18T21:05:00"}` (current is 21:04) | Contains `expiryDate` | 201 | 1-minute expiry accepted |
| BND-013 | Special unicode characters in URL | `{"originalUrl":"https://example.com/café"}` | URL stored/encoded properly | 201 | Unicode handling verified |
| BND-014 | URL with consecutive slashes | `{"originalUrl":"https://example.com//double//slash"}` | URL stored as-is or normalized | 201 | Double slashes handled |
| BND-015 | URL with trailing slash | `{"originalUrl":"https://example.com/path/"}` | URL stored with trailing slash | 201 | Trailing slashes preserved |

---

## 4. VALIDATION TESTS

### Tests for Data Integrity and Business Logic

| Test ID | Test Case | Request Body | Expected Response | Status Code | Assertion |
|---------|-----------|--------------|------------------|-------------|-----------|
| VAL-001 | Duplicate URL submission | Submit same URL twice | Both return different `shortCode` values | 201 | Different codes for same URL |
| VAL-002 | Response format is valid JSON | `{"originalUrl":"https://example.com"}` | Response is parseable JSON | 201 | Valid JSON structure |
| VAL-003 | Verify short code is alphanumeric only | `{"originalUrl":"https://example.com"}` | `shortCode` contains only a-z, A-Z, 0-9 | 201 | Alphanumeric validation |
| VAL-004 | Database ID is positive integer | `{"originalUrl":"https://example.com"}` | Response `id` is integer > 0 | 201 | Valid ID generation |
| VAL-005 | CreatedDate is within last minute | `{"originalUrl":"https://example.com"}` | `createdDate` is current time ±60 seconds | 201 | Timestamp accuracy |
| VAL-006 | URL stored exactly as submitted | `{"originalUrl":"https://example.com/specific?ref=unique"}` | Response `originalUrl` matches input exactly | 201 | URL preservation |
| VAL-007 | Response includes Location header | `{"originalUrl":"https://example.com"}` | HTTP headers include `Location` or `X-Short-Url` | 201 | Standard REST practices |
| VAL-008 | Content-Type in response | `{"originalUrl":"https://example.com"}` | Response header `Content-Type: application/json` | 201 | Correct content type |
| VAL-009 | Case sensitivity in shortCode | Create URL, then GET with different case | May work depending on implementation | Varies | Case sensitivity behavior |
| VAL-010 | URL special chars preserved | `{"originalUrl":"https://example.com/path?a=1&b=2&c=3"}` | Response preserves exact query format | 201 | Query format preservation |
| VAL-011 | Expiry date exactly as submitted | `{"originalUrl":"https://example.com","expiryDate":"2026-12-31T23:59:59"}` | Response contains exact `expiryDate` | 201 | Exact date preservation |
| VAL-012 | No modification of originalUrl | Submit URL with unusual formatting | Response contains exactly as submitted | 201 | No URL normalization |
| VAL-013 | Short code entropy | Generate 100 URLs | All have unique short codes | 201 | Randomness verification |
| VAL-014 | Response time acceptable | Any valid request | Response received within 500ms | 201 | Performance baseline |
| VAL-015 | Database transaction committed | Create URL, immediately query DB | URL exists in database | 201 | Transaction persistence |

---

## 5. SECURITY TESTS

### Tests for Security Vulnerabilities and Threats

| Test ID | Test Case | Request Body | Expected Response | Status Code | Assertion |
|---------|-----------|--------------|------------------|-------------|-----------|
| SEC-001 | SQL Injection - OR condition | `{"originalUrl":"https://example.com' OR '1'='1"}` | URL stored as-is, no SQL execution | 201 | SQL injection prevention |
| SEC-002 | SQL Injection - DROP command | `{"originalUrl":"https://example.com'; DROP TABLE urls;--"}` | URL stored as-is, no SQL execution | 201 | SQL injection prevention |
| SEC-003 | XSS attack - script tag | `{"originalUrl":"https://example.com<script>alert('xss')</script>"}` | URL stored/escaped, no code execution | 201 | XSS prevention |
| SEC-004 | XSS attack - event handler | `{"originalUrl":"https://example.com'><img src=x onerror=alert('xss')>"}` | URL stored safely, no code execution | 201 | Event handler blocking |
| SEC-005 | NoSQL Injection | `{"originalUrl":"https://example.com","originalUrl":{"$ne":null}}` | Handled gracefully, not exploited | 400 or 201 | NoSQL injection prevention |
| SEC-006 | LDAP Injection | `{"originalUrl":"https://example.com*)(|(uid=*"}` | URL stored as-is, no LDAP execution | 201 | LDAP injection prevention |
| SEC-007 | Command Injection | `{"originalUrl":"https://example.com; rm -rf /"}` | URL stored as-is, no command execution | 201 | Command injection prevention |
| SEC-008 | CORS preflight headers | OPTIONS request to endpoint | Response includes appropriate CORS headers or 405 | 200 or 405 | CORS security |
| SEC-009 | No sensitive data in response | Any valid request | Response doesn't contain DB internals, passwords | 201 | Sensitive data protection |
| SEC-010 | No stack trace in error | Invalid request | Error response hides stack trace | 400 | Error message sanitization |
| SEC-011 | Request size limit | Send extremely large JSON (>10MB) | Request rejected or connection closed | 413 | Large payload protection |
| SEC-012 | Malformed JSON bombing | Send deeply nested JSON | Request rejected gracefully | 400 | JSON bomb prevention |
| SEC-013 | Header injection | URL with newline characters `\r\n` | URL stored as-is or newlines removed | 201 | Header injection prevention |
| SEC-014 | XXE injection attempt | `<?xml version="1.0"?><!DOCTYPE foo [...]>` | Request rejected as invalid JSON | 400 | XML external entity prevention |
| SEC-015 | Rate limiting not enforced (future) | Send 1000 requests in 1 second | Requests processed (future: may be throttled) | 201 | Baseline for future rate limiting |
| SEC-016 | No authentication bypass | Missing/invalid auth token | Either processed or 401 (per implementation) | Varies | Authentication enforcement |
| SEC-017 | No horizontal privilege escalation | Create URL with ID of another user's URL | Handled appropriately (future auth) | 201 or 403 | Access control |
| SEC-018 | URL redirection to malicious site | `{"originalUrl":"https://attacker.com/malware"}` | URL stored but user warned on redirect (future) | 201 | Malicious URL handling |
| SEC-019 | Prototype pollution attempt | `{"originalUrl":"https://example.com","__proto__":{"admin":true}}` | Not exploited, object created safely | 201 | Prototype pollution prevention |
| SEC-020 | Path traversal attempt | `{"originalUrl":"https://example.com/../../etc/passwd"}` | URL stored as-is or path normalized safely | 201 | Path traversal prevention |

---

## 6. PERFORMANCE TESTS

### Tests for Performance and Load Handling

| Test ID | Test Case | Scenario | Expected Behavior | Assertion |
|---------|-----------|----------|-------------------|-----------|
| PERF-001 | Single request latency | Create 1 URL | Response time < 500ms | Baseline performance |
| PERF-002 | Average response time (10 requests) | Create 10 URLs sequentially | Avg response time < 300ms | Normal throughput |
| PERF-003 | Peak response time (10 requests) | Create 10 URLs sequentially | Max response time < 1000ms | Acceptable peak |
| PERF-004 | Concurrent requests (10 parallel) | 10 simultaneous create requests | All return 201 within 2 seconds | Concurrency handling |
| PERF-005 | Concurrent requests (100 parallel) | 100 simultaneous create requests | 95%+ return 201 within 5 seconds | High concurrency |
| PERF-006 | Database insert performance | Create URL, measure DB write time | DB write completes within 100ms | DB performance |
| PERF-007 | Index effectiveness on shortCode | Create 1000 URLs, then retrieve | GET request responds within 100ms | Index usage |
| PERF-008 | Memory usage (1000 URLs) | Create 1000 URLs in sequence | Memory usage stable, no leaks | Memory efficiency |
| PERF-009 | CPU usage under load | 100 concurrent requests | CPU spike < 80%, returns to normal | CPU efficiency |
| PERF-010 | Large URL processing (2048 chars) | Create URL with max length | Response time < 500ms | Large URL handling |
| PERF-011 | Response payload size | Any valid request | Response < 1KB | Efficient payload |
| PERF-012 | Throughput (requests/second) | Measure over 60 seconds | ≥ 50 req/sec per instance | Throughput baseline |
| PERF-013 | Database connection pooling | 50 concurrent requests | No connection timeout errors | Connection management |
| PERF-014 | Unique code generation speed | Generate 10,000 unique codes | All unique, < 5 seconds | Code generation |
| PERF-015 | JSON serialization time | Response with 500+ URLs | Serialization < 100ms | JSON performance |

---

## Test Execution Summary

| Category | Total Tests | Critical | High | Medium | Low |
|----------|------------|----------|------|--------|-----|
| Positive Tests | 15 | 3 | 5 | 5 | 2 |
| Negative Tests | 19 | 5 | 8 | 4 | 2 |
| Boundary Tests | 15 | 3 | 6 | 4 | 2 |
| Validation Tests | 15 | 4 | 6 | 4 | 1 |
| Security Tests | 20 | 8 | 8 | 3 | 1 |
| Performance Tests | 15 | 2 | 5 | 6 | 2 |
| **TOTAL** | **99** | **25** | **38** | **26** | **10** |

---

## Recommendations

### Priority 1 (Must Have)
- ✅ Implement all Positive Tests (POS-001 to POS-015)
- ✅ Implement all Negative Tests (NEG-001 to NEG-019)
- ✅ Implement all Security Tests (SEC-001 to SEC-020)

### Priority 2 (Should Have)
- ✅ Implement Validation Tests (VAL-001 to VAL-015)
- ✅ Implement Boundary Tests (BND-001 to BND-015)

### Priority 3 (Nice to Have)
- ✅ Implement Performance Tests (PERF-001 to PERF-015)
- ✅ Implement monitoring and alerting

---

## Test Execution Tools

### Recommended Tools
- **API Testing**: Postman, Insomnia, REST Client (VS Code)
- **Automation**: JUnit 5, RestAssured, MockMvc (Spring)
- **Load Testing**: JMeter, k6, Gatling
- **Security**: OWASP ZAP, Burp Suite (Enterprise)
- **Monitoring**: Spring Boot Actuator, Prometheus

---

**Generated**: 2026-07-18T21:04:48.365+05:30  
**Role**: Senior QA Architect  
**Endpoint**: POST /api/url/shorten  
**Expected Status**: HTTP 201 Created