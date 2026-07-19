# URL Shortener with AI-Assisted Development

A Spring Boot REST API application that shortens long URLs into compact, shareable codes. This project demonstrates Brownfield development practices with AI assistance.

**Repository**: [parmanandmishra/url-shortener-ai](https://github.com/parmanandmishra/url-shortener-ai)

---

## Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
- [Development Setup](#development-setup)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Current Features ✅

- **URL Shortening**: Convert long URLs into 6-character short codes
- **URL Retrieval**: Get original URL from short code
- **Analytics**: Track click count for each shortened URL
- **CRUD Operations**: Create, read, update, and delete URLs
- **Input Validation**: Comprehensive URL format and length validation
- **Error Handling**: Structured error responses with descriptive messages
- **Unique Short Codes**: Collision-free generation with exponential backoff retry
- **RESTful API**: Standard REST principles with proper HTTP status codes
- **Swagger UI**: Interactive API documentation at `/swagger-ui.html`

### Planned Features 🚀

- **URL Expiration**: Set expiration dates on shortened URLs (HTTP 410 Gone for expired)
- **Rate Limiting**: Per-IP request throttling
- **Authentication**: API key and OAuth 2.0 support
- **Batch Operations**: Create multiple short URLs at once
- **Custom Short Codes**: User-defined short codes
- **QR Code Generation**: Generate QR codes for short URLs
- **Webhooks**: Notifications on URL expiration
- **Analytics Dashboard**: Advanced usage statistics

---

## Quick Start

### Prerequisites

- **Java 21** or later
- **Maven 3.8+**
- **PostgreSQL 12+** (or any supported database)
- **Docker** (optional, for containerized deployment)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/parmanandmishra/url-shortener-ai.git
   cd url-shortener-ai/backend
   ```

2. **Configure database** (edit `src/main/resources/application.properties`):
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

3. **Build the application**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

### Docker Deployment

1. **Build Docker image**:
   ```bash
   docker build -t url-shortener:latest .
   ```

2. **Run with Docker Compose**:
   ```bash
   docker-compose up -d
   ```

### First API Call

```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.github.com/parmanandmishra/url-shortener-ai"}'
```

**Response**:
```json
{
  "id": 1,
  "originalUrl": "https://www.github.com/parmanandmishra/url-shortener-ai",
  "shortCode": "rT9pKL",
  "createdDate": "2026-07-18T17:35:11",
  "expiryDate": null,
  "clickCount": 0
}
```

---

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT APPLICATIONS                      │
│              (Web Browser, Mobile App, CLI)                  │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
┌─────────────────────────▼────────────────────────────────────┐
│                    API GATEWAY LAYER                          │
│                                                               │
│  • Request Validation        • Error Handling                │
│  • Authentication (Future)   • Response Formatting           │
└────────────────────────┬────────────────────────────────────┘
                         │
┌─────────────────────────▼──────────────────────────────────────┐
│                   CONTROLLER LAYER                             │
│                                                                │
│  ┌─────────────────────────────────────────────────┐          │
│  │  UrlController                                  │          │
│  │  • POST /api/urls/shorten                       │          │
│  │  • GET /api/urls/{shortCode}                    │          │
│  │  • GET /api/urls/redirect/{shortCode}           │          │
│  │  • PUT /api/urls/{id}                           │          │
│  │  • DELETE /api/urls/{id}                        │          │
│  └─────────────────────────────────────────────────┘          │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌─────────────────────────▼──────────────────────────────────────┐
│                    SERVICE LAYER                              │
│                                                                │
│  ┌──────────────────────────────────────────────┐             │
│  │  UrlService                                  │             │
│  │  • createShortUrl(UrlRequestDto)             │             │
│  │  • getUrlByShortCode(String)                 │             │
│  │  • getOriginalUrl(String)                    │             │
│  │  • updateUrl(Long, UrlRequestDto)            │             │
│  │  • deleteUrl(Long)                           │             │
│  │  • Expiration Validation                     │             │
│  │  • Short Code Generation                     │             │
│  │  • URL Validation                            │             │
│  └──────────────────────────────────────────────┘             │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌─────────────────────────▼──────────────────────────────────────┐
│                  REPOSITORY LAYER                             │
│                                                                │
│  ┌──────────────────────────────────────────────┐             │
│  │  UrlRepository (extends JpaRepository)       │             │
│  │  • findByShortCode(String)                   │             │
│  │  • existsByShortCode(String)                 │             │
│  │  • incrementClickCount(Long)                 │             │
│  │  • [Future] findExpiredUrls()                │             │
│  └──────────────────────────────────────────────┘             │
└────────────────────────┬──────────────────────────────────────┘
                         │ JPA/Hibernate
┌─────────────────────────▼──────────────────────────────────────┐
│                   ENTITY LAYER                                │
│                                                                │
│  ┌──────────────────────────────────────────────┐             │
│  │  UrlMapping                                  │             │
│  │  • id: Long                                  │             │
│  │  • originalUrl: String                       │             │
│  │  • shortCode: String (unique)                │             │
│  │  • createdDate: LocalDateTime                │             │
│  │  • expiryDate: LocalDateTime (nullable)      │             │
│  │  • clickCount: Long                          │             │
│  └──────────────────────────────────────────────┘             │
└────────────────────────┬──────────────────────────────────────┘
                         │ SQL
┌─────────────────────────▼──────────────────────────────────────┐
│                   DATABASE LAYER                              │
│                                                                │
│  ┌──────────────────────────────────────────────┐             │
│  │  PostgreSQL Database                         │             │
│  │  • urls table                                │             │
│  │  • Indexes: shortCode, createdDate, expiryDate            │
│  │  • Constraints: unique(shortCode), NOT NULL checks        │
│  └──────────────────────────────────────────────┘             │
└──────────────────────────────────────────────────────────────┘
```

### Component Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    REST API (Port 8080)                      │
│                                                              │
│  POST   /api/urls/shorten            Create short URL      │
│  GET    /api/urls/{shortCode}        Get URL details       │
│  GET    /api/urls/redirect/{id}      Redirect to original  │
│  PUT    /api/urls/{id}               Update URL            │
│  DELETE /api/urls/{id}               Delete URL            │
└──────────────┬───────────────────────────────────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
┌───▼──────┐         ┌────▼──────┐
│Controller│         │  Exception│
│  Layer   │         │  Handler  │
└───┬──────┘         └────┬──────┘
    │                     │
    └──────────┬──────────┘
               │
    ┌──────────▼──────────┐
    │   Service Layer     │
    │                     │
    │  UrlService         │
    │  - Validation       │
    │  - Logic            │
    │  - Expiration Check │
    └──────────┬──────────┘
               │
    ┌──────────▼──────────┐
    │  Repository Layer   │
    │                     │
    │  UrlRepository      │
    │  - Database queries │
    └──────────┬──────────┘
               │
    ┌──────────▼──────────┐
    │   Entity Layer      │
    │                     │
    │  UrlMapping         │
    └──────────┬──────────┘
               │
    ┌──────────▼──────────┐
    │   PostgreSQL DB     │
    │                     │
    │  urls table         │
    └─────────────────────┘
```

### Data Flow

**Create Short URL Flow**:
```
Request (originalUrl) 
    ↓
Controller validates input
    ↓
Service validates URL format & length
    ↓
Service generates unique short code
    ↓
Entity (UrlMapping) created
    ↓
Repository saves to database
    ↓
Response (UrlResponseDto)
```

**Redirect Flow**:
```
Request (shortCode)
    ↓
Controller receives request
    ↓
Service finds URL by shortCode
    ↓
Service checks expiration
    ↓
Service increments click count
    ↓
Response (originalUrl)
```

---

## API Documentation

### Quick Reference

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/urls/shorten` | Create a short URL |
| GET | `/api/urls/{shortCode}` | Get URL details |
| GET | `/api/urls/redirect/{shortCode}` | Redirect to original URL |
| PUT | `/api/urls/{id}` | Update URL |
| DELETE | `/api/urls/{id}` | Delete URL |

### Full API Documentation

See [API_DOCUMENTATION.md](./Docs/API_DOCUMENTATION.md) for:
- Detailed endpoint specifications
- Request/response examples
- Error codes and handling
- Data model definitions
- Validation rules
- Best practices

### Interactive API Docs

Once the application is running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## Development Setup

### Prerequisites

- Java 21 JDK
- Maven 3.8+
- Git
- PostgreSQL 12+ (for production) or H2 (for testing)

### IDE Setup

#### IntelliJ IDEA

1. Open project: `File` → `Open` → Select project root
2. Configure JDK: `Preferences` → `Project Structure` → Set JDK 21
3. Enable Lombok: `Preferences` → `Plugins` → Install "Lombok"
4. Run configurations: Select `BackendApplication` as main class

#### VS Code

1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. Create `.vscode/launch.json`:
   ```json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "type": "java",
         "name": "Spring Boot App",
         "request": "launch",
         "mainClass": "com.pm.urlshortener.BackendApplication",
         "projectName": "urlshortener",
         "cwd": "${workspaceFolder}/backend",
         "console": "integratedTerminal"
       }
     ]
   }
   ```

### Running Locally

```bash
cd backend

# Run with Maven
mvn spring-boot:run

# Or run as JAR
mvn clean package
java -jar target/urlshortener-0.0.1-SNAPSHOT.jar
```

### Development with H2 Database

Update `application.properties`:
```properties
spring.h2.console.enabled=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
```

Access H2 console: http://localhost:8080/h2-console

---

## Testing

The application has been validated using:

- Maven Build
- Unit Testing (JUnit 5)
- Manual API Testing (Postman)
- Swagger UI Verification
- Code Coverage (JaCoCo)

Detailed results are available in: [TEST_RESULTS.md](./Docs/TEST_RESULTS.md)

### Unit Tests

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=UrlServiceTest
mvn test -Dtest=UrlControllerTest
```

### Integration Tests

```bash
mvn verify
```

### Test Coverage

Generate coverage report:
```bash
mvn test jacoco:report
```

Open report: `target/site/jacoco/index.html`

### Manual Testing with cURL

```bash
# Create URL
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://github.com"}'

# Get details
curl http://localhost:8080/api/urls/rT9pKL

# Redirect
curl -L http://localhost:8080/api/urls/redirect/rT9pKL

# Update
curl -X PUT http://localhost:8080/api/urls/1 \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://example.com"}'

# Delete
curl -X DELETE http://localhost:8080/api/urls/1
```

---

## Project Structure

```
url-shortener-ai/
├── Docs/
│   ├── BROWNFIELD_SCENARIO.md
│   ├── URL_EXPIRATION_IMPACT_ANALYSIS.md
│   ├── API_DOCUMENTATION.md
│   ├── ARCHITECTURE.md
│   └── README.md
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pm/urlshortener/
│   │   │   │   ├── BackendApplication.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── UrlController.java
│   │   │   │   ├── service/
│   │   │   │   │   └── UrlService.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── UrlRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── UrlMapping.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── UrlRequestDto.java
│   │   │   │   │   └── UrlResponseDto.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── UrlNotFoundException.java
│   │   │   │   │   ├── InvalidUrlException.java
│   │   │   │   │   ├── ErrorResponse.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   └── config/
│   │   │   │       └── SwaggerConfig.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │       └── java/com/pm/urlshortener/
│   │           ├── service/
│   │           │   └── UrlServiceTest.java
│   │           ├── controller/
│   │           │   └── UrlControllerTest.java
│   │           └── BackendApplicationTests.java
│   │
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── .gitignore
├── README.md
└── docker-compose.yml
```

---

## Configuration

### Application Properties

Edit `backend/src/main/resources/application.properties`:

```properties
# Server
server.port=8080
spring.application.name=url-shortener

# Database (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.pm.urlshortener=DEBUG

# Swagger/OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# URL Shortener Configuration (Future)
url.expiration.enabled=true
url.expiration.default-days=90
url.expiration.max-days=3650
```

### Environment-Specific Configurations

- `application-dev.properties` - Development (H2 database)
- `application-prod.properties` - Production (PostgreSQL)

Switch active profile:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## Deployment

### Docker Build

```dockerfile
FROM maven:3.8.1-openjdk-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
COPY --from=builder /app/backend/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: url_shortener
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  api:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/url_shortener
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password

volumes:
  pgdata:
```

Deploy:
```bash
docker-compose up -d
```

---

## Performance Considerations

### Database Indexing

The `urls` table includes optimized indexes:

```sql
CREATE INDEX idx_short_code ON urls(shortCode);      -- For lookups
CREATE INDEX idx_created_date ON urls(createdDate);  -- For analytics
CREATE INDEX idx_expiry_date ON urls(expiryDate);    -- For expiration
```

### Query Optimization

- Short code lookups use indexed queries (O(1))
- Pagination support for large result sets
- Connection pooling with HikariCP

### Caching Strategy (Future)

- Redis cache for frequently accessed URLs
- TTL-based invalidation aligned with expiration
- Cache-Control headers for HTTP clients

---

## Troubleshooting

### Database Connection Issues

```bash
# Check PostgreSQL is running
psql -U postgres -d url_shortener

# Reset password if needed
psql -U postgres
\password postgres
```

### Port Already in Use

```bash
# Change port in application.properties
server.port=8081

# Or kill existing process
lsof -i :8080
kill -9 <PID>
```

### Lombok Not Working

```
IntelliJ: Preferences → Plugins → Install Lombok
VS Code: Install "Lombok Annotations Support" extension
```

### Test Failures

```bash
# Run with verbose output
mvn test -X

# Check test logs
cat target/surefire-reports/
```

---

## Contributing

### Coding Standards

- Follow Google Java Style Guide
- Use meaningful variable names
- Write unit tests for new features
- Add JavaDoc for public APIs
- Commit messages: `feat:`, `fix:`, `test:`, `docs:` prefixes

### Brownfield Development with AI

This project demonstrates best practices:

1. **Impact Analysis First**: Understand all affected components
2. **Design Review**: Validate design decisions
3. **Incremental Changes**: Implement features in phases
4. **Comprehensive Testing**: Ensure no regressions
5. **Documentation**: Keep docs in sync with code

See [BROWNFIELD_SCENARIO.md](./Docs/BROWNFIELD_SCENARIO.md) for detailed process.

---

## License

MIT License - See LICENSE file for details

---

## Contact & Support

- **GitHub Issues**: Report bugs or request features
- **Email**: parmanandmishra@gmail.com
- **Documentation**: See `/Docs` directory

---

## Acknowledgments

- Spring Boot framework and community
- PostgreSQL database
- OpenAPI/Swagger for API documentation
- AI-assisted development methodologies

