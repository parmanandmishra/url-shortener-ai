# API Test Execution Results (Postman Collection)

- **Execution Timestamp**: 2026-07-20T00:51:37.256471
- **Collection**: `postman/URLShortener.postman_collection.json`
- **Total Executed**: 24
- **Pass**: 23
- **Fail**: 1

| ID | API | Scenario | Request | Response | HTTP Status | Result |
|----|-----|----------|---------|----------|-------------|--------|
| TC-001 | `POST http://localhost:8080/api/urls/shorten` | Positive Tests / Create Short URL - Valid HTTPS | `{   "originalUrl": "https://github.com/parmanandmishra/url-shortener-ai" }` | `{"id":25,"originalUrl":"https://github.com/parmanandmishra/url-shortener-ai","shortCode":"aEraQB","createdDate":"2026-07-20T00:51:37.217322","expiryDate":null,"clickCount":0}` | 201 | **Pass** |
| TC-002 | `POST http://localhost:8080/api/urls/shorten` | Positive Tests / Create Short URL - Auto Normalize google.com | `{   "originalUrl": "google.com" }` | `{"id":26,"originalUrl":"https://google.com","shortCode":"kp6GzF","createdDate":"2026-07-20T00:51:37.220695","expiryDate":null,"clickCount":0}` | 201 | **Pass** |
| TC-003 | `GET http://localhost:8080/api/urls/kp6GzF` | Positive Tests / Get URL Details - Valid Short Code | `(no body)` | `{"id":26,"originalUrl":"https://google.com","shortCode":"kp6GzF","createdDate":"2026-07-20T00:51:37.220695","expiryDate":null,"clickCount":0}` | 200 | **Pass** |
| TC-004 | `GET http://localhost:8080/api/urls/redirect/kp6GzF` | Positive Tests / Redirect - Valid Short Code | `(no body)` | `https://google.com` | 200 | **Pass** |
| TC-005 | `GET http://localhost:8080/api/urls/analytics/kp6GzF` | Positive Tests / Get Analytics - Valid Short Code | `(no body)` | `{"originalUrl":"https://google.com","shortCode":"kp6GzF","clickCount":1,"createdDate":"2026-07-20T00:51:37.220695","expiryDate":null}` | 200 | **Pass** |
| TC-006 | `PUT http://localhost:8080/api/urls/26` | Positive Tests / Update URL - Valid ID | `{   "originalUrl": "https://www.updated-example.com/new-page" }` | `{"id":26,"originalUrl":"https://www.updated-example.com/new-page","shortCode":"kp6GzF","createdDate":"2026-07-20T00:51:37.220695","expiryDate":null,"clickCount":1}` | 200 | **Pass** |
| TC-007 | `DELETE http://localhost:8080/api/urls/26` | Positive Tests / Delete URL - Valid ID | `(no body)` | `` | 204 | **Pass** |
| TC-008 | `POST http://localhost:8080/api/urls/shorten` | Negative Tests / Create Short URL - Unsupported Protocol | `{   "originalUrl": "ftp://example.com/file" }` | `{"status":400,"message":"URL must start with http:// or https://","timestamp":"2026-07-20T00:51:37.232625","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-009 | `GET http://localhost:8080/api/urls/XXXXXX` | Negative Tests / Get URL Details - Unknown Short Code | `(no body)` | `{"status":404,"message":"Short URL not found: XXXXXX","timestamp":"2026-07-20T00:51:37.234235","path":"/api/urls/XXXXXX"}` | 404 | **Pass** |
| TC-010 | `GET http://localhost:8080/api/urls/redirect/NOTEXIST` | Negative Tests / Redirect - Unknown Short Code | `(no body)` | `{"status":404,"message":"Short URL not found: NOTEXIST","timestamp":"2026-07-20T00:51:37.23562","path":"/api/urls/redirect/NOTEXIST"}` | 404 | **Pass** |
| TC-011 | `GET http://localhost:8080/api/urls/analytics/NOTEXIST` | Negative Tests / Get Analytics - Unknown Short Code | `(no body)` | `{"status":404,"message":"Short URL not found: NOTEXIST","timestamp":"2026-07-20T00:51:37.236906","path":"/api/urls/analytics/NOTEXIST"}` | 404 | **Pass** |
| TC-012 | `PUT http://localhost:8080/api/urls/26` | Negative Tests / Update URL - Invalid Protocol | `{   "originalUrl": "ftp://bad-protocol.com" }` | `{"status":404,"message":"URL not found with id: 26","timestamp":"2026-07-20T00:51:37.239293","path":"/api/urls/26"}` | 404 | **Fail** |
| TC-013 | `DELETE http://localhost:8080/api/urls/9999` | Negative Tests / Delete URL - Nonexistent ID | `(no body)` | `{"status":404,"message":"URL not found with id: 9999","timestamp":"2026-07-20T00:51:37.240914","path":"/api/urls/9999"}` | 404 | **Pass** |
| TC-014 | `POST http://localhost:8080/api/urls/shorten` | Boundary Tests / Create Short URL - Maximum Length (2048) | `{   "originalUrl": "https://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...` | `{"id":27,"originalUrl":"https://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...` | 201 | **Pass** |
| TC-015 | `POST http://localhost:8080/api/urls/shorten` | Boundary Tests / Create Short URL - Over Maximum Length (2049) | `{   "originalUrl": "https://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...` | `{"status":400,"message":"URL must be between 10 and 2048 characters","timestamp":"2026-07-20T00:51:37.245369","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-016 | `GET http://localhost:8080/api/urls/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX` | Boundary Tests / Get URL Details - Long Short Code (50 chars) | `(no body)` | `{"status":404,"message":"Short URL not found: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX","timestamp":"2026-07-20T00:51:37.247288","path":"/api/urls/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"}` | 404 | **Pass** |
| TC-017 | `PUT http://localhost:8080/api/urls/27` | Boundary Tests / Update URL - Maximum Length (2048) | `{   "originalUrl": "https://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...` | `{"id":27,"originalUrl":"https://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...` | 200 | **Pass** |
| TC-018 | `DELETE http://localhost:8080/api/urls/0` | Boundary Tests / Delete URL - ID Zero | `(no body)` | `{"status":404,"message":"URL not found with id: 0","timestamp":"2026-07-20T00:51:37.250243","path":"/api/urls/0"}` | 404 | **Pass** |
| TC-019 | `DELETE http://localhost:8080/api/urls/-1` | Boundary Tests / Delete URL - Negative ID | `(no body)` | `{"status":404,"message":"URL not found with id: -1","timestamp":"2026-07-20T00:51:37.251204","path":"/api/urls/-1"}` | 404 | **Pass** |
| TC-020 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Missing Field | `{}` | `{"status":400,"message":"URL cannot be blank","timestamp":"2026-07-20T00:51:37.25222","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-021 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Null Field | `{   "originalUrl": null }` | `{"status":400,"message":"URL cannot be blank","timestamp":"2026-07-20T00:51:37.253216","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-022 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Whitespace Field | `{   "originalUrl": "   " }` | `{"status":400,"message":"URL must be between 10 and 2048 characters","timestamp":"2026-07-20T00:51:37.254329","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-023 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Invalid JSON (Unquoted Key) | `{originalUrl:"https://example.com"}` | `{"status":400,"message":"Malformed JSON request","timestamp":"2026-07-20T00:51:37.255359","path":"/api/urls/shorten"}` | 400 | **Pass** |
| TC-024 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Invalid JSON (Truncated Payload) | `{   "originalUrl": "https://example.com"` | `{"status":400,"message":"Malformed JSON request","timestamp":"2026-07-20T00:51:37.256292","path":"/api/urls/shorten"}` | 400 | **Pass** |

## 3. Test Scenario Results

| ID | API | Scenario | Expected | Actual | Status |
|----|-----|----------|----------|--------|--------|
| TC-001 | `POST http://localhost:8080/api/urls/shorten` | Positive Tests / Create Short URL - Valid HTTPS | 201 | 201 | **Pass** |
| TC-002 | `POST http://localhost:8080/api/urls/shorten` | Positive Tests / Create Short URL - Auto Normalize google.com | 201 | 201 | **Pass** |
| TC-003 | `GET http://localhost:8080/api/urls/kp6GzF` | Positive Tests / Get URL Details - Valid Short Code | 200 | 200 | **Pass** |
| TC-004 | `GET http://localhost:8080/api/urls/redirect/kp6GzF` | Positive Tests / Redirect - Valid Short Code | 200 | 200 | **Pass** |
| TC-005 | `GET http://localhost:8080/api/urls/analytics/kp6GzF` | Positive Tests / Get Analytics - Valid Short Code | 200 | 200 | **Pass** |
| TC-006 | `PUT http://localhost:8080/api/urls/26` | Positive Tests / Update URL - Valid ID | 200 | 200 | **Pass** |
| TC-007 | `DELETE http://localhost:8080/api/urls/26` | Positive Tests / Delete URL - Valid ID | 204 | 204 | **Pass** |
| TC-008 | `POST http://localhost:8080/api/urls/shorten` | Negative Tests / Create Short URL - Unsupported Protocol | 400 | 400 | **Pass** |
| TC-009 | `GET http://localhost:8080/api/urls/XXXXXX` | Negative Tests / Get URL Details - Unknown Short Code | 404 | 404 | **Pass** |
| TC-010 | `GET http://localhost:8080/api/urls/redirect/NOTEXIST` | Negative Tests / Redirect - Unknown Short Code | 404 | 404 | **Pass** |
| TC-011 | `GET http://localhost:8080/api/urls/analytics/NOTEXIST` | Negative Tests / Get Analytics - Unknown Short Code | 404 | 404 | **Pass** |
| TC-012 | `PUT http://localhost:8080/api/urls/26` | Negative Tests / Update URL - Invalid Protocol | 400 | 404 | **Fail** |
| TC-013 | `DELETE http://localhost:8080/api/urls/9999` | Negative Tests / Delete URL - Nonexistent ID | 404 | 404 | **Pass** |
| TC-014 | `POST http://localhost:8080/api/urls/shorten` | Boundary Tests / Create Short URL - Maximum Length (2048) | 201 | 201 | **Pass** |
| TC-015 | `POST http://localhost:8080/api/urls/shorten` | Boundary Tests / Create Short URL - Over Maximum Length (2049) | 400 | 400 | **Pass** |
| TC-016 | `GET http://localhost:8080/api/urls/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX` | Boundary Tests / Get URL Details - Long Short Code (50 chars) | 404 | 404 | **Pass** |
| TC-017 | `PUT http://localhost:8080/api/urls/27` | Boundary Tests / Update URL - Maximum Length (2048) | 200 | 200 | **Pass** |
| TC-018 | `DELETE http://localhost:8080/api/urls/0` | Boundary Tests / Delete URL - ID Zero | 404 | 404 | **Pass** |
| TC-019 | `DELETE http://localhost:8080/api/urls/-1` | Boundary Tests / Delete URL - Negative ID | 404 | 404 | **Pass** |
| TC-020 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Missing Field | 400 | 400 | **Pass** |
| TC-021 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Null Field | 400 | 400 | **Pass** |
| TC-022 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Whitespace Field | 400 | 400 | **Pass** |
| TC-023 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Invalid JSON (Unquoted Key) | 400 | 400 | **Pass** |
| TC-024 | `POST http://localhost:8080/api/urls/shorten` | Validation Tests / Create Short URL - Invalid JSON (Truncated Payload) | 400 | 400 | **Pass** |


## 4. Execution outcome

Total executed: 24
• Pass: 23
• Fail: 1
The single failure is TC-012 (Negative Tests / Update URL - Invalid Protocol): expected 400, actual 404 because the same record ID was deleted earlier in the run, so update hits not-found before protocol validation.

## 5. Coverage Summary

- Positive Scenarios ✅
- Negative Scenarios ✅
- Boundary Tests ✅
- Validation Tests ✅
- Error Handling ✅

## 6. AI Usage 

GitHub Copilot assisted with:

- Test scenario generation
- Postman collection generation
- Assertion generation

Engineer responsibilities:

- Reviewed generated tests
- Executed all test cases
- Validated responses
- Approved final results

## 7. Lessons Learned

- AI significantly accelerated test case generation.
- Human validation ensured correctness and prevented false positives.
- Combining AI-assisted generation with engineer review improved both productivity and quality.