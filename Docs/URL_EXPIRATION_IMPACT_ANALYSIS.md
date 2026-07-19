# URL Expiration Feature - Impact Analysis

## Overview
Adding URL Expiration functionality to an existing deployed URL Shortener application requires careful consideration of backward compatibility and comprehensive changes across multiple layers.

## Current State
- **Entity**: `UrlMapping` already has `expiryDate` field (optional, nullable)
- **Service**: Expiration validation logic already exists in `getAndValidateUrlByShortCode()`
- **DTO Response**: `UrlResponseDto` already includes `expiryDate` field
- **Database**: Already has index on `expiryDate` column
- **Tests**: Already includes one test for expired URL scenario

## Impacted Components

### 1. **Database Layer**
**Impact Level**: MEDIUM
- **Schema Changes**:
  - ✅ Column `expiryDate` already exists (nullable)
  - **Migration Required**: 
    - Add NOT NULL constraint consideration (backward compatibility issue)
    - Ensure index exists on `expiryDate` for query performance
    - Consider adding index for querying expired URLs for cleanup operations
  - **Data Integrity**:
    - Existing URLs will have NULL `expiryDate` (no expiration)
    - No migration needed for existing data if NULL means "never expires"
    - May need cleanup job for orphaned expired records

---

### 2. **Entity Layer** (`UrlMapping.java`)
**Impact Level**: LOW
- **Current State**: 
  - ✅ `expiryDate` field already exists
  - ✅ Index already exists: `@Index(name = "idx_expiry_date", columnList = "expiryDate")`
  - No changes required

---

### 3. **Request DTO** (`UrlRequestDto.java`)
**Impact Level**: MEDIUM
- **Current Issue**: Does not include `expiryDate` field for creating/updating URLs with expiration
- **Required Changes**:
  - Add optional `expiryDate` field to request DTO
  - Add validation:
    - Ensure provided date is in the future
    - Set reasonable expiration limits (e.g., max 10 years)
    - Optional: TTL parameter (e.g., "30 days")
  - Add JavaDoc and swagger annotations
- **Design Decision Needed**:
  - Accept absolute date/time or relative TTL (e.g., "1d", "7d", "30d")?
  - Should expiration be optional on creation?

---

### 4. **Response DTO** (`UrlResponseDto.java`)
**Impact Level**: LOW
- **Current State**: 
  - ✅ `expiryDate` field already exists
  - No changes required

---

### 5. **Repository Layer** (`UrlRepository.java`)
**Impact Level**: MEDIUM
- **Current State**: Basic CRUD operations only
- **Required Additions**:
  - Query to find all expired URLs (for cleanup/maintenance):
    ```java
    @Query("SELECT u FROM UrlMapping u WHERE u.expiryDate IS NOT NULL AND u.expiryDate < CURRENT_TIMESTAMP")
    List<UrlMapping> findExpiredUrls();
    ```
  - Query to find non-expired URLs:
    ```java
    @Query("SELECT u FROM UrlMapping u WHERE u.expiryDate IS NULL OR u.expiryDate >= CURRENT_TIMESTAMP")
    List<UrlMapping> findActiveUrls();
    ```
  - Method to delete expired URLs (for scheduled cleanup)
  - Pagination support for cleanup operations

---

### 6. **Service Layer** (`UrlService.java`)
**Impact Level**: HIGH
- **Current Implementation**:
  - ✅ Expiration validation exists in `getAndValidateUrlByShortCode()` (lines 94-96)
  - Returns `UrlNotFoundException` for expired URLs
  
- **Required Enhancements**:
  - **Create/Update Logic**:
    - Accept optional `expiryDate` from DTO
    - Validate expiration date is in future
    - Handle both absolute date and relative TTL formats
    - Business logic for setting expiration policies
  
  - **Enhanced Exception Handling**:
    - Consider custom exception `UrlExpiredException` vs current `UrlNotFoundException`
    - HTTP 410 (Gone) response should be returned for expired URLs (not 404)
  
  - **New Operations**:
    - `deleteExpiredUrls()` - Remove expired URLs (scheduled job)
    - `getExpiringUrls(days)` - Get URLs expiring within N days
    - `extendUrlExpiration(shortCode, newExpiryDate)` - Allow extending expiration
  
  - **Analytics/Reporting**:
    - Method to get expiration statistics
    - Track expired vs active URLs

---

### 7. **Controller Layer** (`UrlController.java`)
**Impact Level**: HIGH
- **Endpoints to Enhance**:
  - **POST /api/urls/shorten**:
    - Accept optional `expiryDate` in request
    - Return `expiryDate` in response
    - Add API documentation for expiration parameter
  
  - **GET /api/urls/{shortCode}**:
    - Return 410 (Gone) for expired URLs instead of 404
    - Add `X-Expires` header with expiration information
  
  - **GET /api/urls/redirect/{shortCode}**:
    - Check expiration before redirect
    - Return 410 (Gone) for expired URLs
    - Add `Cache-Control` headers considering expiration
  
  - **PUT /api/urls/{id}**:
    - Allow updating expiration date
    - Add validation for new date
  
  - **New Endpoints (Optional)**:
    - `PATCH /api/urls/{id}/extend-expiry` - Extend expiration
    - `GET /api/urls/expiring-soon` - Get expiring URLs
    - `DELETE /api/urls/expired` - Admin endpoint to clean expired URLs

- **Swagger Documentation**:
  - ✅ Document new `expiryDate` field in requests/responses
  - Document 410 (Gone) response for expired URLs
  - Provide examples for expiration scenarios
  - Add business rules about expiration in descriptions

---

### 8. **Exception Handling** (`GlobalExceptionHandler.java`)
**Impact Level**: MEDIUM
- **Current State**: Treats expired URLs as `UrlNotFoundException` (404)
  
- **Required Changes**:
  - **Option 1 (Breaking Change)**: Return HTTP 410 for expired URLs
    - Create `UrlExpiredException` extends `RuntimeException`
    - Add handler for `UrlExpiredException` returning 410 (Gone)
    - Modify service to throw `UrlExpiredException` instead of `UrlNotFoundException`
  
  - **Option 2 (Backward Compatible)**: Keep 404, add custom response
    - Include `reason: "EXPIRED"` or `code: "URL_EXPIRED"` in error response
    - Clients can differentiate between "not found" vs "expired"

---

### 9. **Test Coverage** (`UrlServiceTest.java` & `UrlControllerTest.java`)
**Impact Level**: HIGH
- **Current Coverage**:
  - ✅ One test for expired URL (service level)
  - ❌ No tests for expiration creation/updates
  - ❌ No tests for multiple expiration scenarios
  - ❌ No controller tests for 410 response

- **Required New Tests**:
  
  **Service Tests**:
  - `testCreateShortUrl_WithExpiration_Success()`
  - `testCreateShortUrl_WithPastDate_Failure()`
  - `testCreateShortUrl_WithFutureDate_Success()`
  - `testGetOriginalUrl_Expired_ThrowsException()`
  - `testGetUrlByShortCode_ExpiringWithinHour()`
  - `testUpdateUrl_WithNewExpiration()`
  - `testDeleteExpiredUrls_Success()`
  - `testExtendUrlExpiration_Success()`
  
  **Controller Tests**:
  - `testGetUrlByShortCode_Expired_Returns410()`
  - `testRedirectToOriginalUrl_Expired_Returns410()`
  - `testCreateShortUrl_WithExpiration_IncludesInResponse()`
  - `testUpdateUrl_WithNewExpiration_Success()`
  - `testCreateShortUrl_WithInvalidFutureDate_Returns400()`

---

### 10. **API Documentation & Swagger** (`SwaggerConfig.java`)
**Impact Level**: LOW
- **Required Updates**:
  - Update API documentation with expiration feature
  - Add example scenarios in Swagger UI
  - Document HTTP 410 response
  - Add timestamp format examples (ISO 8601)
  - Document business rules (min/max expiration periods)

---

### 11. **Configuration & Properties**
**Impact Level**: MEDIUM
- **Potential Configuration Parameters**:
  - `url.expiration.enabled` - Feature flag
  - `url.expiration.default-days` - Default expiration (if not specified)
  - `url.expiration.max-days` - Maximum allowed expiration period
  - `url.expiration.min-days` - Minimum allowed expiration period
  - `url.cleanup.enabled` - Auto-cleanup expired URLs
  - `url.cleanup.schedule` - Cron expression for cleanup job
  
- **Files to Create/Modify**:
  - `application.properties` or `application.yml`
  - Create `ExpirationProperties` class with `@ConfigurationProperties`

---

### 12. **Background Jobs & Maintenance** (NEW)
**Impact Level**: MEDIUM
- **Required Components**:
  - **Scheduled Task**:
    - Daily job to delete expired URLs
    - Optional: Archive expired URLs instead of deleting
    - Logging of cleanup operations
  
  - **Implementation**:
    - Use `@Scheduled` annotation with cron expression
    - Create `ExpirationCleanupService` class
    - Handle transaction management
    - Batch processing for large datasets

---

### 13. **Caching Implications**
**Impact Level**: MEDIUM
- **Considerations**:
  - Cache headers must respect expiration dates
  - `Cache-Control: max-age=X` should align with URL expiration
  - Redis/cache entries should auto-expire
  - Stale cache might serve expired URLs

---

### 14. **Client Integration & API Versioning**
**Impact Level**: HIGH (if deployed already)
- **Backward Compatibility Issues**:
  - Clients expecting 404 for missing URLs will now get 410 for expired
  - New `expiryDate` field in responses (safe - backward compatible)
  - Optional `expiryDate` in requests (safe - backward compatible)

- **Versioning Strategy**:
  - Consider API versioning (/v2/urls vs /api/urls)
  - Add feature flag for gradual rollout
  - Document breaking changes (410 vs 404)

---

### 15. **Database Performance & Indexes**
**Impact Level**: MEDIUM
- **Current Indexes**:
  - ✅ `idx_expiry_date` already exists
  
- **Additional Considerations**:
  - Index on `(expiryDate, id)` for efficient range queries
  - Composite index for queries filtering by both status and expiry
  - Monitor query performance for cleanup operations
  - Consider partitioning if table becomes very large

---

### 16. **Monitoring & Logging**
**Impact Level**: LOW
- **Required Additions**:
  - Log when URLs are created with expiration
  - Log when URLs expire (on access attempt)
  - Monitor cleanup job execution
  - Alert on failed cleanup jobs
  - Track expiration-related errors

---

## Summary Table

| Component | Impact | Status | Required Changes |
|-----------|--------|--------|-------------------|
| Database | MEDIUM | Ready | Index verification, migration strategy |
| Entity | LOW | ✅ Complete | None |
| Request DTO | MEDIUM | Incomplete | Add `expiryDate` field + validation |
| Response DTO | LOW | ✅ Complete | None |
| Repository | MEDIUM | Incomplete | Add queries for expired/active URLs |
| Service | HIGH | Partial | Enhance with full expiration logic |
| Controller | HIGH | Incomplete | Add endpoints, update responses (410) |
| Exception Handler | MEDIUM | Incomplete | Add 410 (Gone) response handling |
| Tests | HIGH | Incomplete | Add comprehensive expiration tests |
| Swagger | LOW | Incomplete | Document expiration scenarios |
| Config | MEDIUM | Not Started | Add expiration parameters |
| Scheduled Jobs | MEDIUM | Not Started | Create cleanup service |
| Caching | MEDIUM | Review | Align cache with expiration |
| Versioning | HIGH | Consider | API versioning strategy |
| Monitoring | LOW | Minimal | Add logging for expiration events |

---

## Implementation Priority

**Phase 1 (Critical)**:
1. Request DTO - Accept expiration dates
2. Service Layer - Complete expiration logic
3. Controller - Return 410 for expired URLs
4. Tests - Add expiration test cases

**Phase 2 (Important)**:
1. Repository - Add expiration queries
2. Exception Handling - Proper 410 responses
3. Configuration - Externalize expiration rules
4. API Documentation - Update Swagger

**Phase 3 (Maintenance)**:
1. Scheduled Cleanup Job
2. Monitoring & Alerts
3. Performance Optimization
4. Cache Headers

---

## Backward Compatibility Notes

- ✅ **Safe**: New optional `expiryDate` in requests
- ✅ **Safe**: `expiryDate` field in responses
- ⚠️ **Breaking**: HTTP 410 instead of 404 for expired URLs
- ⚠️ **Breaking**: If cleanup job deletes old data

**Recommendation**: Use feature flag to control 410 response during gradual rollout.

