## High Level Solution Architecture

```text
                           +-----------------------+
                           |     React Frontend    |
                           |  (Material UI / React)|
                           +-----------+-----------+
                                       |
                                       | REST API
                                       |
                           +-----------v-----------+
                           | Spring Boot REST API  |
                           |    Controller Layer   |
                           +-----------+-----------+
                                       |
                                       |
                           +-----------v-----------+
                           |    Service Layer      |
                           | Business Logic        |
                           +-----------+-----------+
                                       |
                          +------------+-------------+
                          |                          |
                          |                          |
              +-----------v-----------+   +----------v---------+
              | Repository Layer      |   | Validation Layer   |
              | Spring Data JPA       |   | URL Validation     |
              +-----------+-----------+   +--------------------+
                          |
                          |
                +---------v----------+
                |   PostgreSQL DB    |
                | URL Mapping Store  |
                +--------------------+
```

## Layered Architecture

```text
+----------------------------------------------------+
|                  Presentation Layer                |
|             React + REST Controller                |
+----------------------------------------------------+

+----------------------------------------------------+
|                 Business Layer                     |
|          URL Service / Analytics Service           |
+----------------------------------------------------+

+----------------------------------------------------+
|               Persistence Layer                    |
|        Spring Data JPA Repository Layer            |
+----------------------------------------------------+

+----------------------------------------------------+
|                 Database Layer                     |
|                  PostgreSQL                        |
+----------------------------------------------------+
```

## Request Processing Flow

```text
User

  │

  ▼

React UI

  │

  ▼

REST Controller

  │

  ▼

Business Service

  │

  ▼

Repository

  │

  ▼

PostgreSQL

  │

  ▼

Repository

  │

  ▼

Service

  │

  ▼

Controller

  │

  ▼

JSON Response
```

## URL Shortening Workflow

```text
User enters Long URL

        │

        ▼

Validate URL

        │

        ▼

Generate Unique Short Code

        │

        ▼

Store Mapping

(Long URL ↔ Short Code)

        │

        ▼

Return Short URL
```

## URL Redirection Workflow

```text
User clicks Short URL

        │

        ▼

Controller receives Request

        │

        ▼

Find Original URL

        │

        ▼

Increment Click Count

        │

        ▼

HTTP 302 Redirect

        │

        ▼

Original Website Opens
```

## Project Structure

```text
url-shortener-ai/

│

├── backend/

│   ├── controller/

│   ├── service/

│   ├── repository/

│   ├── entity/

│   ├── dto/

│   ├── exception/

│   ├── config/

│   └── util/

│

├── frontend/

│

├── docs/

│

├── tests/

│

└── README.md
```

## Backend Package Structure

```text
com.pm.urlshortener

│

├── controller

├── service

├── repository

├── entity

├── dto

├── config

├── util

├── exception

└── UrlShortenerApplication
```

# High Level Architecture

![Solution Architecture](images/Architecture.png)

# Request Flow

![Request Flow](images/RequestFlow.png)

# Database Design

![Database Design](images/Database.png)
