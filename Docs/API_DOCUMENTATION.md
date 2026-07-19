# URL Shortener API Documentation

## Overview

The URL Shortener API provides REST endpoints for creating, retrieving, updating, and deleting shortened URLs. This document describes all available endpoints, request/response formats, and error handling.

**Base URL**: `http://localhost:8080/api/urls`

**API Version**: 1.0

**Content-Type**: `application/json`

---

## Table of Contents

1. [Authentication](#authentication)
2. [Error Handling](#error-handling)
3. [Data Models](#data-models)
4. [Endpoints](#endpoints)
5. [Rate Limiting](#rate-limiting)
6. [Examples](#examples)

---

## Requirements Coverage

This API document is aligned to the assessment requirements and covers the full URL shortener lifecycle.

### Functional Coverage

| Requirement | Document Coverage |
|---|---|
| Create Short URL | `POST /api/urls/shorten` endpoint and success response |
| Store URL Mapping | Create endpoint response, persistence model, and database-backed storage |
| Generate Unique Short Code | Short code field definition and create endpoint behavior |
| Validate URLs | Request DTO constraints and validation rules |
| Handle Invalid Request | Error handling section and validation/error examples |
| Track Click Count | Redirect endpoint notes and click count response field |
| URL Expiry | `expiryDate` field, validation rules, and expired resource behavior |

### Non-Functional Coverage

| Requirement | Document Coverage |
|---|---|
| URL shortening ≤ 500 ms | Rate limiting / performance expectations and assessment documentation |
| URL redirection ≤ 200 ms | Redirect endpoint behavior and performance expectations |
| Concurrent requests | Stateless REST design and operational assumptions |
| Scalability | Layered architecture, indexing, and future enhancements |
| Security | Validation, sanitization, and error handling |
| Maintainability | Clear endpoint contracts and model separation |

---

## Authentication

Currently, the API does not require authentication. Future versions will implement API key or OAuth 2.0 authentication.

---

## Error Handling

All errors are returned as JSON with the following structure:

```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2026-07-18T17:35:11.797+05:30",
  "path": "/api/urls/shorten"
}
```

### HTTP Status Codes

| Status | Description |
|--------|-------------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created successfully |
| 204 | No Content - Delete successful |
| 400 | Bad Request - Invalid input |
| 404 | Not Found - Resource not found |
| 410 | Gone - URL expired (planned feature) |
| 500 | Internal Server Error |

---

## Data Models

### UrlRequestDto

**Request body for creating or updating a short URL**

```json
{
  "originalUrl": "https://www.example.com/very/long/path/to/resource",
  "expiryDate": "2026-12-31T23:59:59"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `originalUrl` | String | Yes | Length: 10-2048 chars, Must start with http:// or https://, Valid URL format |
| `expiryDate` | LocalDateTime | No | Must be in future, ISO 8601 format (YYYY-MM-DDTHH:mm:ss) |

**Example (Create with expiration)**:
```json
{
  "originalUrl": "https://www.example.com/page?ref=abc123",
  "expiryDate": "2026-09-18T17:35:11"
}
```

**Example (Create without expiration)**:
```json
{
  "originalUrl": "https://www.example.com/permanent-link"
}
```

---

### UrlResponseDto

**Response body for URL information**

```json
{
  "id": 1,
  "originalUrl": "https://www.example.com/very/long/path",
  "shortCode": "abc123",
  "createdDate": "2026-07-18T15:30:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 42
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique identifier in database |
| `originalUrl` | String | The original long URL |
| `shortCode` | String | 6-character unique short code |
| `createdDate` | LocalDateTime | When the short URL was created |
| `expiryDate` | LocalDateTime | When the short URL expires (null = never) |
| `clickCount` | Long | Number of times the short URL was accessed |

---

## Endpoints

### 1. Create Short URL

Create a new shortened URL with optional expiration.

```
POST /api/urls/shorten
```

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "originalUrl": "https://www.example.com/page",
  "expiryDate": "2026-12-31T23:59:59"
}
```

**Success Response (201 Created)**:
```json
{
  "id": 1,
  "originalUrl": "https://www.example.com/page",
  "shortCode": "abc123",
  "createdDate": "2026-07-18T15:30:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 0
}
```

**Error Responses**:

- **400 Bad Request** - Invalid URL format
  ```json
  {
    "status": 400,
    "message": "Invalid URL format: no protocol specified",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/shorten"
  }
  ```

- **400 Bad Request** - URL too long
  ```json
  {
    "status": 400,
    "message": "URL exceeds maximum length of 2048 characters",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/shorten"
  }
  ```

- **400 Bad Request** - Missing required field
  ```json
  {
    "status": 400,
    "message": "URL cannot be blank",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/shorten"
  }
  ```

---

### 2. Get URL Details by Short Code

Retrieve detailed information about a shortened URL.

```
GET /api/urls/{shortCode}
```

**Path Parameters**:
- `shortCode` (string, required): 6-character short code

**Success Response (200 OK)**:
```json
{
  "id": 1,
  "originalUrl": "https://www.example.com/page",
  "shortCode": "abc123",
  "createdDate": "2026-07-18T15:30:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 42
}
```

**Error Responses**:

- **404 Not Found** - Short code doesn't exist
  ```json
  {
    "status": 404,
    "message": "Short URL not found: xyz789",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/xyz789"
  }
  ```

- **410 Gone** - URL has expired (future feature)
  ```json
  {
    "status": 410,
    "message": "Short URL has expired: abc123",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/abc123"
  }
  ```

---

### 3. Redirect to Original URL

Get the original URL and increment click count. This endpoint is used for actual redirects.

```
GET /api/urls/redirect/{shortCode}
```

**Path Parameters**:
- `shortCode` (string, required): 6-character short code

**Success Response (200 OK)**:
```
https://www.example.com/page?ref=abc123
```

**Error Responses**:

- **404 Not Found** - Short code doesn't exist
  ```json
  {
    "status": 404,
    "message": "Short URL not found: xyz789",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/redirect/xyz789"
  }
  ```

- **410 Gone** - URL has expired
  ```json
  {
    "status": 410,
    "message": "Short URL has expired: abc123",
    "timestamp": "2026-07-18T17:35:11",
    "path": "/api/urls/redirect/abc123"
  }
  ```

**Note**: This endpoint increments the `clickCount` on successful retrieval.

---

### 4. Get URL by ID

Retrieve URL information using the database ID.

```
GET /api/urls/{id}
```

**Path Parameters**:
- `id` (long, required): Database ID

**Success Response (200 OK)**:
```json
{
  "id": 1,
  "originalUrl": "https://www.example.com/page",
  "shortCode": "abc123",
  "createdDate": "2026-07-18T15:30:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 42
}
```

**Error Response**:
- **404 Not Found** - ID doesn't exist

---

### 5. Update URL

Update the original URL associated with a given ID.

```
PUT /api/urls/{id}
```

**Path Parameters**:
- `id` (long, required): Database ID

**Request Body**:
```json
{
  "originalUrl": "https://www.example.com/new-target",
  "expiryDate": "2027-06-18T17:35:11"
}
```

**Success Response (200 OK)**:
```json
{
  "id": 1,
  "originalUrl": "https://www.example.com/new-target",
  "shortCode": "abc123",
  "createdDate": "2026-07-18T15:30:00",
  "expiryDate": "2027-06-18T17:35:11",
  "clickCount": 42
}
```

**Error Responses**:

- **404 Not Found** - ID doesn't exist

- **400 Bad Request** - Invalid URL format

---

### 6. Delete URL

Delete a URL record by its ID.

```
DELETE /api/urls/{id}
```

**Path Parameters**:
- `id` (long, required): Database ID

**Success Response (204 No Content)**:
```
(Empty body)
```

**Error Response**:
- **404 Not Found** - ID doesn't exist

---

## Planned Endpoints (URL Expiration Feature)

The following endpoints will be added in future versions:

### Extend URL Expiration
```
PATCH /api/urls/{id}/extend-expiry
```

Body:
```json
{
  "newExpiryDate": "2027-12-31T23:59:59"
}
```

### Get Expiring URLs
```
GET /api/urls/expiring-soon?days=7
```

Query Parameters:
- `days` (integer): Number of days to look ahead

### Delete Expired URLs (Admin)
```
DELETE /api/urls/expired
```

---

## Rate Limiting

Currently, no rate limiting is enforced. Future versions will implement rate limiting:
- 100 requests per minute per IP address
- 1000 requests per hour per IP address

---

## Examples

### Example 1: Create a Temporary Link

**Request**:
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "originalUrl": "https://docs.example.com/confidential-report.pdf",
    "expiryDate": "2026-07-25T17:35:11"
  }'
```

**Response**:
```json
{
  "id": 42,
  "originalUrl": "https://docs.example.com/confidential-report.pdf",
  "shortCode": "rT9pKL",
  "createdDate": "2026-07-18T17:35:11",
  "expiryDate": "2026-07-25T17:35:11",
  "clickCount": 0
}
```

---

### Example 2: Create a Permanent Link

**Request**:
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "originalUrl": "https://github.com/parmanandmishra/url-shortener-ai"
  }'
```

**Response**:
```json
{
  "id": 43,
  "originalUrl": "https://github.com/parmanandmishra/url-shortener-ai",
  "shortCode": "mXq8Yw",
  "createdDate": "2026-07-18T17:35:11",
  "expiryDate": null,
  "clickCount": 0
}
```

---

### Example 3: Get URL Details

**Request**:
```bash
curl -X GET http://localhost:8080/api/urls/rT9pKL \
  -H "Content-Type: application/json"
```

**Response**:
```json
{
  "id": 42,
  "originalUrl": "https://docs.example.com/confidential-report.pdf",
  "shortCode": "rT9pKL",
  "createdDate": "2026-07-18T17:35:11",
  "expiryDate": "2026-07-25T17:35:11",
  "clickCount": 5
}
```

---

### Example 4: Redirect (Click)

**Request**:
```bash
curl -X GET http://localhost:8080/api/urls/redirect/rT9pKL
```

**Response**:
```
https://docs.example.com/confidential-report.pdf
```

---

### Example 5: Update URL

**Request**:
```bash
curl -X PUT http://localhost:8080/api/urls/42 \
  -H "Content-Type: application/json" \
  -d '{
    "originalUrl": "https://docs.example.com/updated-report.pdf",
    "expiryDate": "2026-08-18T17:35:11"
  }'
```

**Response**:
```json
{
  "id": 42,
  "originalUrl": "https://docs.example.com/updated-report.pdf",
  "shortCode": "rT9pKL",
  "createdDate": "2026-07-18T17:35:11",
  "expiryDate": "2026-08-18T17:35:11",
  "clickCount": 5
}
```

---

### Example 6: Delete URL

**Request**:
```bash
curl -X DELETE http://localhost:8080/api/urls/42
```

**Response**:
```
HTTP/1.1 204 No Content
```

---

## API Response Headers

### Response Headers

```
Content-Type: application/json
Date: Fri, 18 Jul 2026 17:35:11 GMT
Server: Apache Tomcat/10.1.x
Transfer-Encoding: chunked
```

### Custom Headers (Planned)

Future versions will include:
- `X-RateLimit-Limit`: Maximum requests per window
- `X-RateLimit-Remaining`: Requests remaining in window
- `X-RateLimit-Reset`: Timestamp when limit resets
- `X-Expires`: Expiration timestamp for the resource
- `Cache-Control`: Caching directives based on expiration

---

## Validation Rules

### URL Validation

- **Length**: 10-2048 characters
- **Protocol**: Must start with `http://` or `https://`
- **Format**: Must be a valid URL (RFC 3986 compliant)

**Valid URLs**:
- ✅ `https://example.com`
- ✅ `https://example.com/path/to/resource`
- ✅ `https://example.com/path?query=value&other=123`
- ✅ `https://example.com:8080/secure/path`

**Invalid URLs**:
- ❌ `example.com` (missing protocol)
- ❌ `ftp://example.com` (unsupported protocol)
- ❌ `http://` (incomplete URL)
- ❌ Empty string

### Expiration Validation (Future)

- **Must be in future**: Can't set expiration to past date
- **Reasonable limits**:
  - Minimum: 1 minute from now
  - Maximum: 10 years from now
- **Format**: ISO 8601 (YYYY-MM-DDTHH:mm:ss)

---

## Best Practices

### For API Consumers

1. **Error Handling**: Always check HTTP status codes
2. **Expiration**: Check `expiryDate` in response; set appropriate cache headers
3. **URL Encoding**: Ensure `originalUrl` is properly formatted
4. **Short Code Format**: Short codes are case-sensitive (a-z, A-Z, 0-9)
5. **Caching**: Don't cache redirect responses; respect `Cache-Control` headers

### For Developers

1. **Backward Compatibility**: The 410 response for expired URLs is a breaking change
2. **Timestamps**: All timestamps are in ISO 8601 UTC format
3. **IDs**: Short codes are unique; database IDs may be used for updates/deletes
4. **Click Counting**: Only incremented on successful redirect (GET /redirect/)
5. **Transactions**: All operations are ACID-compliant

---

## OpenAPI/Swagger

The API is documented using OpenAPI 3.0 specification. Access the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI JSON schema is available at:

```
http://localhost:8080/v3/api-docs
```

---

## Changelog

### Version 1.0 (Current)
- Create short URLs
- Retrieve URL details
- Redirect to original URL
- Update URLs
- Delete URLs
- Click counting
- Expiration validation (basic)

### Version 1.1 (Planned)
- HTTP 410 (Gone) for expired URLs
- Request DTO expiration support
- Extend expiration endpoint
- Expiring URLs query endpoint
- Admin cleanup endpoint
- Rate limiting
- API key authentication

### Version 2.0 (Future)
- OAuth 2.0 support
- Analytics endpoints
- Batch operations
- Custom short codes
- QR code generation
- Webhooks for expiration events
