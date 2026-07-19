# URL Shortener API Documentation

This document is generated from the current Spring Boot controller and DTO contracts in:

- `backend/src/main/java/com/pm/urlshortener/controller/UrlController.java`
- `backend/src/main/java/com/pm/urlshortener/dto/UrlRequestDto.java`
- `backend/src/main/java/com/pm/urlshortener/dto/UrlResponseDto.java`
- `backend/src/main/java/com/pm/urlshortener/dto/UrlAnalyticsDto.java`
- `backend/src/main/java/com/pm/urlshortener/exception/GlobalExceptionHandler.java`

## Base Path

`/api/urls`

## Endpoint Summary

| Method | Path | Description |
|---|---|---|
| POST | `/api/urls/shorten` | Create short URL |
| GET | `/api/urls/shorten` | Explicitly not allowed route (returns 405) |
| GET | `/api/urls/{shortCode}` | Get URL details by short code |
| GET | `/api/urls/redirect/{shortCode}` | Resolve original URL and increment click count |
| GET | `/api/urls/analytics/{shortCode}` | Get analytics without incrementing click count |
| PUT | `/api/urls/{id}` | Update original URL and optional expiry |
| DELETE | `/api/urls/{id}` | Delete URL record |

---

## Data Contracts

### Request DTO (`UrlRequestDto`)

```json
{
  "originalUrl": "https://example.com/articles/ai-testing",
  "expiryDate": "2026-12-31T23:59:59"
}
```

Validation rules:

- `originalUrl` is required (`@NotBlank`)
- `originalUrl` length must be 10..2048 (`@Length`)
- `expiryDate` is optional; if present must be future date-time (`@Future`)

### Response DTO (`UrlResponseDto`)

```json
{
  "id": 1,
  "originalUrl": "https://example.com/articles/ai-testing",
  "shortCode": "aB12Cd",
  "createdDate": "2026-07-20T10:00:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 0
}
```

### Analytics DTO (`UrlAnalyticsDto`)

```json
{
  "originalUrl": "https://example.com/articles/ai-testing",
  "shortCode": "aB12Cd",
  "clickCount": 15,
  "createdDate": "2026-07-20T10:00:00",
  "expiryDate": null
}
```

### Error DTO (`ErrorResponse`)

```json
{
  "status": 400,
  "message": "URL cannot be blank",
  "timestamp": "2026-07-20T10:00:00",
  "path": "/api/urls/shorten"
}
```

---

## Endpoint Details

### 1) POST `/api/urls/shorten`

Create a short URL from a long URL.

**Request Body:** `UrlRequestDto`  
**Success:** `201 Created` with `UrlResponseDto`

**Example Request**
```json
{
  "originalUrl": "https://example.com/page",
  "expiryDate": "2026-12-31T23:59:59"
}
```

**Example Success Response (`201`)**
```json
{
  "id": 10,
  "originalUrl": "https://example.com/page",
  "shortCode": "xYz123",
  "createdDate": "2026-07-20T10:00:00",
  "expiryDate": "2026-12-31T23:59:59",
  "clickCount": 0
}
```

### 2) GET `/api/urls/shorten`

Explicit not-allowed route to prevent collision with `/{shortCode}`.

**Response:** `405 Method Not Allowed` (empty body)

### 3) GET `/api/urls/{shortCode}`

Get URL details for a known short code.

**Success:** `200 OK` with `UrlResponseDto`

### 4) GET `/api/urls/redirect/{shortCode}`

Returns original URL text and increments click count.

**Success:** `200 OK` with plain string body (original URL)

### 5) GET `/api/urls/analytics/{shortCode}`

Get analytics for a short code (does not increment click count).

**Success:** `200 OK` with `UrlAnalyticsDto`

### 6) PUT `/api/urls/{id}`

Update an existing URL record and optional expiry date.

**Request Body:** `UrlRequestDto`  
**Success:** `200 OK` with updated `UrlResponseDto`

### 7) DELETE `/api/urls/{id}`

Delete an existing URL record.

**Success:** `204 No Content`

---

## HTTP Status Behavior

### Success

- `200 OK`
- `201 Created`
- `204 No Content`

### Error (via controller advice / exception mapping)

- `400 Bad Request` (validation failure, malformed JSON, type mismatch, invalid URL)
- `404 Not Found` (unknown short code/id/path)
- `405 Method Not Allowed` (unsupported method)
- `406 Not Acceptable` (unsupported `Accept` negotiation)
- `410 Gone` (expired short URL)
- `415 Unsupported Media Type` (unsupported `Content-Type`)
- `500 Internal Server Error` (unhandled exception fallback)

---

## Error Mapping Source

`GlobalExceptionHandler` maps:

- `MethodArgumentNotValidException` -> `400`
- `HttpMessageNotReadableException` -> `400`
- `MethodArgumentTypeMismatchException` -> `400`
- `InvalidUrlException` -> `400`
- `UrlNotFoundException` -> `404`
- `NoResourceFoundException` -> `404`
- `HttpRequestMethodNotSupportedException` -> `405`
- `HttpMediaTypeNotAcceptableException` -> `406`
- `UrlExpiredException` -> `410`
- `HttpMediaTypeNotSupportedException` -> `415`
- `Exception` -> `500`

---

## Observability Notes

- Response header `X-Request-Id` is added per request for correlation.
- Health endpoint is available at `/actuator/health` (Actuator configured to expose health).

