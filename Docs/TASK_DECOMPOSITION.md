# Engineering Task Breakdown

Before implementation, the problem was decomposed into logical engineering work items.

This approach allows AI tools to generate smaller, more accurate outputs while enabling incremental validation.

## Phase 1 — Requirement Analysis

Tasks

Understand problem statement\
Identify functional requirements\
Identify non-functional requirements\
Identify assumptions\
Identify risks

Deliverables

Requirement Analysis Document

## Phase 2 — Solution Design

Tasks

Choose technology stack\
Design architecture\
Design APIs\
Design database\
Define package structure

Deliverables

Architecture Diagram\
Database Design\
API Specification

## Phase 3 — Backend Development

Tasks

Domain Layer\
URL Entity\
DTOs\
Repository Layer\
URL Repository\
Service Layer\
Short URL Generation\
URL Lookup\
Analytics\
Validation\
Controller Layer

REST APIs

POST /shorten\
GET /{shortCode}\
GET /analytics/{shortCode}

## Phase 4 — Frontend

Tasks

URL Input Screen\
Display Short URL\
Error Handling\
Copy Button

## Phase 5 — Testing

Tasks

Unit Tests\
Integration Tests\
API Testing\
Coverage Report

## Phase 6 — Documentation

Tasks

README\
Architecture\
AI Engineering Log\
Brownfield Scenario\
Greenfield Scenario

## AI Task Decomposition

The implementation was further decomposed for AI-assisted development.

| Task | AI Used |	Human Review |
| --- | --- | --- |
| Architecture	| Claude	| Yes |
| DTO Generation	|Copilot	| Yes |
| Repository	|Copilot	| Yes |
| Controller	|Copilot	| Yes |
| Unit Tests	|Claude |	Yes |
| Documentation	|Claude |	Yes | 

## Risks

| Risk |	Mitigation |
| --- | --- | 
| Duplicate URLs |	Unique Index |
|Invalid URL |	Validation |
| AI hallucination |	Manual Review |
| Poor prompts |	Iterative Prompt Engineering |
|Security Issues	| Human validation |

## Development Order 
Requirement

↓

Architecture

↓

Database

↓

Entity

↓

Repository

↓

Service

↓

Controller

↓

Testing

↓

Documentation
