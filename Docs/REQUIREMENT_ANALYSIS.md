Project: AI Assisted URL Shortener

Author: Parmanand Mishra

Date: 18-Jul-2026

# 1. Objective

The objective of this assessment is to design and develop a production-ready URL Shortener application while demonstrating the effective use of Generative AI throughout the Software Development Lifecycle (SDLC).

The assessment evaluates not only software development skills but also the ability to leverage AI for planning, implementation, testing, documentation, and engineering decision-making.

# 2. Problem Statement

Develop a web-based URL Shortener application that allows users to convert long URLs into shorter, unique URLs.

The application should support URL creation, storage, retrieval, and redirection while following modern engineering practices and demonstrating AI-assisted software development.

# 3. Business Goal

The system should provide users with a simple and efficient way to create compact URLs that are easy to share and manage.

From an engineering perspective, the solution should demonstrate:

Scalable architecture
Clean code
AI-assisted development
Production readiness
Good engineering judgement

# 4. Functional Requirements
   
The application shall support the following features.

| ID | Requirement | Priority |
|----------|-----------|-----------|
| FR1 | Create Short URL | High |
| FR1 | Store URL Mapping | High |
| FR1 | Generate Unique Short Code | High |
| FR1 | Validate URLs | High |
| FR1 | Handle Invalid Request| High |
| FR1 | Track Click Count | Medium |
| FR1 | URL Expiry | Low |

 
# 5. Non Functional Requirements
   
URL shortening should complete within 500 ms\
URL redirection should complete within 200 ms\
APIs should remain responsive under concurrent requests

**<u>Scalability</u>**

The architecture should support:\
Millions of URL records\
Stateless services\
Horizontal scaling\
Future cloud deployment\
Reliability\
High availability\
Graceful exception handling\
Data consistency\
Duplicate prevention\

**<u>Security</u>**

URL validation\
Input sanitization\
SQL Injection prevention\
XSS prevention\
Secure API design

**<u>Maintainability</u>**

The code should follow:\
SOLID Principles\
Clean Architecture\
Separation of Concerns\
Modular Design\
Reusable Components\

**<u>Testability</u>**

The application should support:
Unit Testing\
Integration Testing\
Mocking\
High Code Coverage

**<u>Observability</u>**

Structured logging\
Exception handling\
Health endpoints\
Metrics support

# 6. AI Engineering Goals

This assessment is also intended to demonstrate AI-assisted software engineering.

AI will be used for:

Requirement understanding\
Task decomposition\
Architecture suggestions\
Code generation\
Unit test generation\
Documentation\
Refactoring\
Code review

Human engineering judgement will be applied before accepting AI-generated outputs.

# 7. Stakeholders

Primary Stakeholders

End User\
Engineering Team\
Product Owner

Secondary Stakeholders

QA Team\
DevOps Team\
Operations Team

# 8. Assumptions

The following assumptions have been made:

No authentication is required.\
URLs are publicly accessible.\
One short URL maps to one original URL.\
Click analytics only records click count.\
URL expiry is optional.\
PostgreSQL is sufficient for persistence.\
Redis caching is not required for this assessment.

# 9. Constraints
    
Limited implementation time.\
AI-generated code must be reviewed before acceptance.\
The solution should remain simple while demonstrating engineering quality.\
Production deployment is not mandatory.

# 10. Risks
    
Risk	Mitigation\
Duplicate short URLs	Unique index\
Invalid URL	Validation\
AI Hallucination	Manual review\
Security Issues	Input validation\
Future Scalability	Layered Architecture\

# 11. Out of Scope
    
The following features are intentionally excluded:

User Login\
User Registration\
QR Code Generation\
Billing\
Admin Portal\
Distributed Cache\
Multi-region Deployment\
CDN\
Kubernetes Deployment

# 12. Acceptance Criteria
    
The solution will be considered successful if:

Functional requirements are implemented.\
REST APIs work correctly.\
AI usage is clearly documented.\
Code follows engineering best practices.\
Unit tests pass successfully.\
Documentation is complete.\
Architecture supports future scalability.

# 13. Engineering Approach

The solution will be implemented using an AI-assisted development approach where Generative AI is used as an engineering accelerator rather than an autonomous developer.

The development process will follow:

Requirement Analysis\
Task Decomposition\
Solution Architecture\
AI-assisted Implementation\
Human Validation\
Testing\
Documentation

All AI-generated outputs will be reviewed, refined, and validated before being incorporated into the final solution.

# 14. Key Engineering Decisions

| Decision | Rationale |
|----------|-----------|
| Spring Boot | Mature enterprise framework with strong REST and dependency injection support. |
| React | Lightweight, component-based UI suitable for a simple frontend. |
| PostgreSQL | Reliable relational database with ACID compliance and indexing support. |
| Layered Architecture | Improves maintainability, separation of concerns, and testability. |
| REST APIs | Standard interface for client-server communication. |
| GitHub Copilot + Claude | Used to accelerate development while ensuring all outputs are reviewed manually. |
| JUnit + Mockito | Enables automated testing and supports high code coverage. |
