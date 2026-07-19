# Brownfield Development Scenario



# Scenario

Assume the existing URL Shortener application has already been deployed.

The Product Owner requests the following enhancement.

> Add URL Expiration functionality.

---

# Engineering Process

Rather than immediately implementing the feature, AI was first used to understand the impact.

---

# Step 1 – Impact Analysis

Prompt

> Identify all application components impacted by adding URL Expiration.

AI Identified

1. **Database schema/data model**
    - `urls.expiry_date` column semantics (nullable vs required), indexing, migration/backfill strategy, cleanup policy.

2. **Domain entity**
    - `backend/src/main/java/com/pm/urlshortener/entity/UrlMapping.java` (`expiryDate` field + index behavior).

3. **API request/response contracts (DTOs)**
    - `UrlRequestDto` (to accept expiration input if user-configurable).
    - `UrlResponseDto`, `UrlAnalyticsDto` (expiration visibility in responses).

4. **Repository layer**
    - `backend/src/main/java/com/pm/urlshortener/repository/UrlRepository.java`
    - Expired/active query methods, cleanup/batch operations.

5. **Service layer (core business logic)**
    - `backend/src/main/java/com/pm/urlshortener/service/UrlService.java`
    - Expiry validation on resolve/redirect, create/update expiry rules, optional cleanup/extension logic.

6. **Controller/API behavior**
    - `backend/src/main/java/com/pm/urlshortener/controller/UrlController.java`
    - Expired URL behavior per endpoint (especially redirect/read), status contract (`404` vs `410 Gone` decision), Swagger annotations.

7. **Exception model + global error handling**
    - `backend/src/main/java/com/pm/urlshortener/exception/*`
    - If `410 Gone` is required, add/route dedicated expiration exception in `GlobalExceptionHandler`.

8. **Automated tests**
    - Service/controller/repository tests for: expired vs non-expired, boundary dates, create/update with expiry, analytics behavior, error status mapping.

9. **API documentation and test artifacts**
    - `Docs/API_DOCUMENTATION.md`, `Docs/API_TEST_PLAN.md`, Swagger/OpenAPI responses/examples, execution/report docs.

10. **Configuration & operations**
- `application.properties` feature toggles/default TTL/max TTL, scheduled cleanup cadence, retention behavior.

11. **Background maintenance jobs (if enabled)**
- Scheduler/cleanup component for expired records, batching, transactional safety.

12. **Observability & compatibility**
- Logging/metrics for expiration events and cleanup jobs.
- Client impact/versioning if response semantics change (notably `404` → `410`).

Human Validation

Confirmed all impacted components.

---

# Step 2 – Design Review

Before implementation the following design decisions were made.

Database

Add

expiry_date

Entity

Add

expiryDate

Business Logic

Validate URL expiration before redirect.

Controller

Return HTTP 410 (Gone) for expired URLs.

---

# Step 3 – AI Assisted Code Changes

AI generated

- Entity modification
- Repository update
- Service update
- Controller update
- Tests

All generated code was manually reviewed before acceptance.

---

# Step 4 – Regression Testing

Existing functionality was verified.

Validated

- URL Creation
- URL Redirect
- Analytics

New Tests

- Expired URL
- Non Expired URL

---

# Engineering Decision

AI was primarily used to accelerate impact analysis and repetitive code generation.

The overall design, validation strategy and regression testing remained under engineering control.

---

# Lessons Learned

Brownfield development requires significantly more engineering judgement than Greenfield development because changes must preserve existing functionality while introducing new features.
