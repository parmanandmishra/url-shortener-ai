# Brownfield Development Scenario

## Objective

This section demonstrates how AI can assist in enhancing an existing application.

Unlike Greenfield development, Brownfield engineering requires understanding an existing codebase before making changes.

---

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

- Database
- Entity
- Repository
- Service
- Controller
- Unit Tests
- Swagger Documentation

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
