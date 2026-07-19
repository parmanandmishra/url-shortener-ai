# URL Shortener - Architecture Documentation

## Overview

This document provides a comprehensive explanation of the URL Shortener application architecture, including system design, component interactions, data flow, and design patterns employed.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Layered Architecture](#layered-architecture)
3. [Component Interactions](#component-interactions)
4. [Data Models](#data-models)
5. [Design Patterns](#design-patterns)
6. [Database Design](#database-design)
7. [Error Handling](#error-handling)
8. [Scalability & Performance](#scalability--performance)
9. [Security Considerations](#security-considerations)
10. [Future Enhancements](#future-enhancements)

---

## Architecture Overview

### Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Framework** | Spring Boot | 3.3.4 |
| **Language** | Java | 21 |
| **Database** | PostgreSQL | 12+ |
| **Build Tool** | Maven | 3.8+ |
| **API Docs** | OpenAPI/Swagger | 3.0 |
| **Data Validation** | Hibernate Validator | 8.0+ |
| **ORM** | Hibernate/JPA | 6.0+ |

### Architectural Style

The application follows a **Layered (N-Tier) Architecture** pattern:

```
┌─────────────────────────────────────────────────┐
│         Presentation Layer (REST API)           │
│                                                 │
│  • UrlController                               │
│  • Exception Handling                          │
│  • Request/Response Formatting                 │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│        Business Logic Layer (Service)            │
│                                                 │
│  • UrlService                                  │
│  • URL Validation                              │
│  • Short Code Generation                       │
│  • Expiration Logic                            │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│      Data Access Layer (Repository)             │
│                                                 │
│  • UrlRepository                               │
│  • Database Queries                            │
│  • Query Optimization                          │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│    Persistence Layer (JPA/Hibernate)            │
│                                                 │
│  • Entity Mapping                              │
│  • ORM Operations                              │
│  • Transaction Management                      │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│       Database Layer (PostgreSQL)               │
│                                                 │
│  • URLs Table                                  │
│  • Indexes                                     │
│  • Constraints                                 │
└─────────────────────────────────────────────────┘
```

---

## Layered Architecture

### 1. Presentation Layer (Controller)

**File**: `UrlController.java`

**Responsibilities**:
- Receive HTTP requests
- Validate request data
- Call service methods
- Return formatted responses
- Handle HTTP status codes

**Key Methods**:
```java
POST   /api/urls/shorten              → createShortUrl()
GET    /api/urls/{shortCode}          → getUrlByShortCode()
GET    /api/urls/redirect/{shortCode} → redirectToOriginalUrl()
PUT    /api/urls/{id}                 → updateUrl()
DELETE /api/urls/{id}                 → deleteUrl()
```

**Technology**:
- Spring `@RestController`
- `@RequestMapping` for URL patterns
- `@Valid` for input validation
- OpenAPI `@Operation` annotations

**Characteristics**:
- ✅ Stateless
- ✅ Request/response conversion
- ✅ HTTP status code management
- ✅ Exception delegation to GlobalExceptionHandler

---

### 2. Business Logic Layer (Service)

**File**: `UrlService.java`

**Responsibilities**:
- Core business logic
- Data validation
- Short code generation
- Expiration checking
- Click count updates
- Transaction management

**Key Methods**:
```java
createShortUrl(UrlRequestDto)         // Create new short URL
getUrlByShortCode(String)             // Retrieve URL details
getOriginalUrl(String)                // Get URL and increment clicks
updateUrl(Long, UrlRequestDto)        // Update URL
deleteUrl(Long)                       // Delete URL
generateUniqueShortCode()             // Generate collision-free code
validateUrl(String)                   // Validate URL format
getAndValidateUrlByShortCode(String)  // Fetch and validate
```

**Validation Logic**:
```
1. URL Format Validation
   ├─ Check if URL is not null/empty
   ├─ Check length (10-2048 chars)
   ├─ Verify starts with http:// or https://
   └─ Parse URL using java.net.URL

2. Expiration Validation
   ├─ Check if expiryDate exists
   └─ Compare with current time
      ├─ If past: throw UrlNotFoundException
      └─ If future: allow access

3. Short Code Generation
   ├─ Generate random 6-char code
   ├─ Check uniqueness in database
   ├─ Retry with exponential backoff
   └─ Max 5 attempts
```

**Transaction Management**:
```java
@Service
@Transactional  // Method-level ACID guarantees
public class UrlService {
    
    @Transactional(readOnly = true)  // Read-only optimization
    public UrlResponseDto getUrlByShortCode(String shortCode) { }
    
    @Transactional                    // Full transaction
    public String getOriginalUrl(String shortCode) { }
}
```

---

### 3. Data Access Layer (Repository)

**File**: `UrlRepository.java`

**Responsibilities**:
- Abstract database operations
- Query definition
- Caching coordination (future)

**Methods**:
```java
findByShortCode(String)     // Find by unique short code
existsByShortCode(String)   // Check existence
incrementClickCount(Long)   // Atomic counter update
save(UrlMapping)            // Create/Update
delete(UrlMapping)          // Delete
findById(Long)              // Find by ID
```

**Advanced Queries (Planned)**:
```java
@Query("SELECT u FROM UrlMapping u WHERE " +
       "u.expiryDate IS NOT NULL AND " +
       "u.expiryDate < CURRENT_TIMESTAMP")
List<UrlMapping> findExpiredUrls();

@Query("SELECT u FROM UrlMapping u WHERE " +
       "u.expiryDate IS NULL OR " +
       "u.expiryDate >= CURRENT_TIMESTAMP")
List<UrlMapping> findActiveUrls();
```

**Technology**:
- Spring Data JPA
- Method naming conventions
- `@Query` for custom queries
- `@Modifying` for UPDATE/DELETE

---

### 4. Entity/Domain Layer

**File**: `UrlMapping.java`

**Structure**:
```java
@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode", unique = true),
    @Index(name = "idx_created_date", columnList = "createdDate"),
    @Index(name = "idx_expiry_date", columnList = "expiryDate")
})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // Auto-generated primary key
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;             // Original long URL
    
    @Column(nullable = false, unique = true)
    private String shortCode;               // 6-char unique code
    
    @Column(nullable = false)
    private LocalDateTime createdDate;      // Creation timestamp
    
    @Column
    private LocalDateTime expiryDate;       // Optional expiration
    
    @Column(nullable = false)
    private Long clickCount = 0L;           // Access counter
}
```

**Design Decisions**:
- `originalUrl` stored as TEXT (supports up to 2048 chars)
- `expiryDate` nullable (NULL = never expires)
- `shortCode` unique (prevents duplicates)
- Indexes on frequently queried columns

---

### 5. DTO Layer (Data Transfer Objects)

#### Request DTO

**File**: `UrlRequestDto.java`

```java
@Data
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be blank")
    @Length(min = 10, max = 2048, message = "URL must be between 10 and 2048 characters")
    private String originalUrl;
}
```

**Current Limitation**: Does not include `expiryDate` (will be added)

#### Response DTO

**File**: `UrlResponseDto.java`

```java
@Data
public class UrlResponseDto {
    private Long id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;       // Already included
    private Long clickCount;
}
```

**Purpose**:
- ✅ Decouples API from entity structure
- ✅ Controls which fields are exposed
- ✅ Enables versioning
- ✅ Simplifies transformation logic

---

### 6. Exception Handling Layer

**Files**: `GlobalExceptionHandler.java`, `ErrorResponse.java`

**Exception Hierarchy**:
```
RuntimeException
├── UrlNotFoundException
│   └── Used for: URL not found OR expired
├── InvalidUrlException
│   └── Used for: Invalid URL format/length
└── Other exceptions → INTERNAL_SERVER_ERROR (500)
```

**Error Response Format**:
```json
{
  "status": 404,
  "message": "Short URL not found: abc123",
  "timestamp": "2026-07-18T17:35:11+05:30",
  "path": "/api/urls/abc123"
}
```

**Current Issue**: 
- Expired URLs return 404 (same as not found)
- Should return 410 (Gone) per HTTP spec

**Planned Enhancement**:
```java
// Create new exception
public class UrlExpiredException extends RuntimeException { }

// Add handler
@ExceptionHandler(UrlExpiredException.class)
public ResponseEntity<ErrorResponse> handleUrlExpiredException(...) {
    return new ResponseEntity<>(errorResponse, HttpStatus.GONE);  // 410
}
```

---

## Component Interactions

### Create URL Flow

```
┌─────────────────────────────────────────────────────┐
│ 1. HTTP Request                                     │
│ POST /api/urls/shorten                             │
│ Body: { "originalUrl": "https://example.com" }     │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 2. Controller (UrlController)                      │
│ • Receive request                                  │
│ • Validate input (via @Valid)                      │
│ • Call service.createShortUrl()                    │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 3. Service (UrlService)                            │
│ • validateUrl(originalUrl)                         │
│ • generateUniqueShortCode()                        │
│ • Create UrlMapping entity                         │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 4. Repository (UrlRepository)                      │
│ • Call urlRepository.save(urlMapping)              │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 5. Database (PostgreSQL)                           │
│ INSERT INTO urls (originalUrl, shortCode, ...)     │
│ RETURNING id                                       │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 6. Response                                        │
│ HTTP 201 Created                                   │
│ Body: { id, originalUrl, shortCode, ... }         │
└─────────────────────────────────────────────────────┘
```

### Get & Redirect Flow

```
┌─────────────────────────────────────────────────────┐
│ 1. HTTP Request                                     │
│ GET /api/urls/redirect/abc123                      │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 2. Controller (UrlController)                      │
│ • Call service.getOriginalUrl(shortCode)           │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 3. Service (UrlService)                            │
│ • Call getAndValidateUrlByShortCode()              │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 4. Validation                                      │
│ ├─ Check: URL exists?                             │
│ │  └─ No → throw UrlNotFoundException (404)        │
│ │                                                  │
│ └─ Check: Is expired?                             │
│    ├─ expiryDate != NULL                          │
│    ├─ expiryDate < now()                          │
│    └─ Yes → throw UrlNotFoundException (404/410)  │
└────────────────────┬────────────────────────────────┘
                     │ (If valid)
┌────────────────────▼────────────────────────────────┐
│ 5. Update Click Count                              │
│ • Call repository.incrementClickCount(id)          │
│ • Atomic: UPDATE urls SET clickCount = clickCount+1│
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│ 6. Response                                        │
│ HTTP 200 OK                                        │
│ Body: { originalUrl }                             │
└─────────────────────────────────────────────────────┘
```

---

## Data Models

### Database Schema

```sql
CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    original_url TEXT NOT NULL,
    short_code VARCHAR(255) NOT NULL UNIQUE,
    created_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP,
    click_count BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT url_check CHECK (char_length(original_url) >= 10),
    CONSTRAINT url_check_2 CHECK (char_length(original_url) <= 2048),
    CONSTRAINT short_code_check CHECK (char_length(short_code) = 6)
);

CREATE INDEX idx_short_code ON urls(short_code);
CREATE INDEX idx_created_date ON urls(created_date);
CREATE INDEX idx_expiry_date ON urls(expiry_date);
```

### Entity Relationships

**Current**: Single entity, no relationships
**Future**: May add:
- User (for authentication)
- Analytics (for detailed stats)
- AuditLog (for compliance)

---

## Design Patterns

### 1. **Layered Architecture Pattern**
Separates concerns into distinct layers with clear responsibilities.

### 2. **Repository Pattern**
Abstracts data access logic behind an interface.

```java
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);
}
```

### 3. **Data Transfer Object (DTO) Pattern**
Transfers data between layers without exposing internal entities.

```java
// External API contract (DTO)
UrlRequestDto & UrlResponseDto

// Internal representation (Entity)
UrlMapping
```

### 4. **Service Layer Pattern**
Centralizes business logic and transaction management.

```java
@Service
@Transactional
public class UrlService { }
```

### 5. **Exception Translation Pattern**
Translates lower-level exceptions to domain-specific exceptions.

```java
// Spring Data throws EmptyResultDataAccessException
// UrlService catches and throws UrlNotFoundException
```

### 6. **Builder Pattern**
Used in DTOs and entities for object construction.

```java
UrlMapping.builder()
    .originalUrl("https://...")
    .shortCode("abc123")
    .createdDate(LocalDateTime.now())
    .build();
```

---

## Database Design

### Indexes

```
Index Name          | Column(s)     | Type      | Purpose
────────────────────┼───────────────┼───────────┼─────────────────
idx_short_code      | shortCode     | UNIQUE    | Fast lookup by code
idx_created_date    | createdDate   | Regular   | Analytics queries
idx_expiry_date     | expiryDate    | Regular   | Expiration queries
(planned)           | expiryDate,id | Composite | Range + ID queries
```

### Query Performance

**High-frequency query** (redirect):
```sql
SELECT * FROM urls WHERE short_code = ? AND (expiry_date IS NULL OR expiry_date >= NOW());
```
- Uses `idx_short_code` (O(log n))
- Execution time: <5ms

**Maintenance query** (cleanup, planned):
```sql
SELECT * FROM urls WHERE expiry_date IS NOT NULL AND expiry_date < NOW();
```
- Uses `idx_expiry_date` (O(log n))
- Batch processing for large deletes

---

## Error Handling

### Exception Hierarchy

```
Throwable
└── Exception
    └── RuntimeException
        ├── UrlNotFoundException (API Error)
        ├── InvalidUrlException (API Error)
        └── Others → Mapped to 500 Internal Server Error
```

### Error Codes (Planned)

```
200 - OK / Success
201 - Created
204 - No Content / Delete Successful
400 - Bad Request (validation error)
404 - Not Found (missing URL or expired, currently)
410 - Gone (URL expired, planned)
500 - Internal Server Error
```

### Structured Error Response

```json
{
  "status": 400,
  "message": "URL must be between 10 and 2048 characters",
  "timestamp": "2026-07-18T17:35:11+05:30",
  "path": "/api/urls/shorten",
  "errorCode": "INVALID_URL_LENGTH"  // Planned
}
```

---

## Scalability & Performance

### Current Optimizations

1. **Database Indexing**
   - Shortcode lookup: O(log n)
   - Unique constraint: Prevents duplicates
   - Indexes on common filter columns

2. **Query Optimization**
   - `@Transactional(readOnly = true)` for read operations
   - Native queries where needed
   - Batch updates for click counts (future)

3. **Connection Pooling**
   - HikariCP (Spring Boot default)
   - Default: 10 connections
   - Configurable via properties

### Scalability Enhancements (Planned)

1. **Caching Layer**
   ```
   Request → Redis Cache → Database
   ```
   - Cache frequent URLs
   - TTL aligned with expiration
   - Invalidation on updates

2. **Database Sharding**
   - Shard by shortCode prefix
   - Horizontal scaling

3. **Read Replicas**
   - Primary for writes
   - Replicas for analytics

4. **Async Processing**
   - Queue cleanup jobs
   - Non-blocking operations
   - Message broker (RabbitMQ/Kafka)

### Load Testing Projections

**Single Instance** (8GB RAM, 4 CPU):
- Throughput: ~10,000 requests/sec
- Latency: <100ms (p99)
- Max concurrent: 500 users

**With Redis Cache**:
- Throughput: ~50,000 requests/sec
- Latency: <20ms (p99)

---

## Security Considerations

### Current Implementation

✅ **Input Validation**
- URL format validation
- Length constraints
- Type checking

### Planned Security Enhancements

**Authentication**
- API Key authentication
- OAuth 2.0 support
- JWT tokens

**Authorization**
- User-based URL access
- Admin operations
- Rate limiting per user

**Data Protection**
- HTTPS enforcement
- SQL injection prevention (via JPA)
- XSS prevention (JSON format)
- CSRF tokens (for web UI)

**Audit & Compliance**
- Audit logging
- Data retention policies
- GDPR compliance
- PII handling

---

## Future Enhancements

### Phase 1: URL Expiration Feature
- **Impact**: 16 components affected
- **Timeline**: 2-4 weeks
- **Details**: See [URL_EXPIRATION_IMPACT_ANALYSIS.md](./URL_EXPIRATION_IMPACT_ANALYSIS.md)

### Phase 2: Authentication & Authorization
- API key support
- User accounts
- Per-user URL quotas

### Phase 3: Advanced Features
- Custom short codes
- Analytics dashboard
- QR code generation
- Webhook notifications
- Batch operations

### Phase 4: Enterprise Features
- High availability (multi-region)
- Advanced caching (Redis)
- Message queues (RabbitMQ)
- Microservices decomposition

---

## Architecture Decision Records (ADRs)

### ADR-001: Layered Architecture
**Decision**: Use 4-tier layered architecture

**Rationale**:
- Clear separation of concerns
- Easy to test layers independently
- Standard for Spring Boot applications
- Familiar to Java developers

**Consequences**:
- ✅ High maintainability
- ✅ Easy to extend
- ⚠️ Performance overhead from layer traversal
- ⚠️ May become over-engineered for simple operations

---

### ADR-002: Short Code Generation
**Decision**: Generate random 6-character codes with collision detection

**Rationale**:
- Shorter than UUID
- Human-readable
- Easy to share verbally
- Sufficient uniqueness (62^6 ≈ 56 trillion combinations)

**Collision Handling**:
- Detect duplicates via unique constraint
- Retry up to 5 times with exponential backoff
- Fail explicitly if unable to generate

**Consequences**:
- ✅ Short URLs (6 chars)
- ⚠️ Retries add latency under high load
- ⚠️ Customization difficult (address in Phase 2)

---

### ADR-003: Nullable Expiration Date
**Decision**: Make `expiryDate` nullable (NULL = never expires)

**Rationale**:
- Supports both temporary and permanent URLs
- Backward compatible with existing data
- No migration needed

**Consequences**:
- ✅ Flexible
- ✅ Backward compatible
- ⚠️ Logic complexity (NULL checks)
- ⚠️ Database storage overhead

---

## Deployment Architecture

### Development
```
Developer Laptop
├── IDE (IntelliJ/VS Code)
├── Spring Boot App (Port 8080)
└── Local H2 Database
```

### Production (Single Instance)
```
Internet
  ↓
Load Balancer (Nginx)
  ↓
Spring Boot App (Port 8080)
  ↓
PostgreSQL Database
  ↓
Persistent Volumes
```

### High Availability (Future)
```
Internet
  ↓
Load Balancer
  ↓
┌──────────────┬──────────────┬──────────────┐
│ App Server 1 │ App Server 2 │ App Server 3 │
└──────┬───────┴──────┬───────┴──────┬───────┘
       │              │              │
       └──────────────┼──────────────┘
                      ↓
        ┌─────────────────────────────┐
        │  PostgreSQL (Primary)       │
        │  + Read Replicas (2x)       │
        │  + Redis Cache              │
        └─────────────────────────────┘
```

---

## Conclusion

The URL Shortener application uses a **clean, layered architecture** that prioritizes:
- **Maintainability**: Clear separation of concerns
- **Extensibility**: Easy to add new features
- **Testability**: Layers can be tested independently
- **Performance**: Optimized queries and indexes

The planned URL Expiration feature will enhance the system's capabilities while maintaining architectural integrity.

