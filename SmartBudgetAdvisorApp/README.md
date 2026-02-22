# SmartBudgetAdvisorApp

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Two-service Spring Boot microservices architecture for a smart budget management system. Both services use Java 21, Maven, Spring Data JPA, Spring Security, Thymeleaf, and MySQL.

- **SmartBudgetApp** (port 8080) — Main application: user auth, accounts, budgets, transactions, email notifications. Connects to the advisor service via Spring Cloud OpenFeign with Resilience4j circuit breaker.
- **SmartBudgetAdvisorApp** (port 8090) — Standalone advice microservice providing budget insights via REST API (`/api/advice`).

## Build & Run Commands

### SmartBudgetApp (no wrapper — requires `mvn` in PATH)
```bash
cd SmartBudgetApp
mvn clean install          # build
mvn spring-boot:run        # run (port 8080)
mvn test                   # run all tests
mvn -Dtest=UserServiceTest test   # run single test class
```

### SmartBudgetAdvisorApp (has Maven wrapper)
```bash
cd SmartBudgetAdvisorApp
./mvnw clean install       # build
./mvnw spring-boot:run     # run (port 8090)
./mvnw test                # run all tests
./mvnw -Dtest=AdviceServiceImplTest test   # run single test class
```

### Prerequisites
- Java 21
- MySQL on localhost:3306 with databases `smartbudget_db` and `smartbudget_advisor_db`
- Start SmartBudgetAdvisorApp before SmartBudgetApp (Feign client dependency)

## Architecture

Both apps follow a layered architecture under `bg.softuni.*`:

```
controllers → services (interface) → impl → repositories → entities
```

### SmartBudgetApp-specific layers
- `clients/` — Feign client (`AdvisorServiceClient`) + fallback for advisor service communication
- `init/DataInitializer` — Seeds roles (ADMIN/USER), currencies, and categories on startup
- `scheduler/EmailScheduledTasks` — Scheduled email notifications via Spring Mail (Gmail SMTP)
- `web/exceptions/` — `GlobalExceptionHandler` and `ResourceNotFoundException`

### Inter-service communication
SmartBudgetApp calls SmartBudgetAdvisorApp at `http://localhost:8090/api/advice` using OpenFeign. Circuit breaker configured with 50% failure threshold. Fallback class provides degraded response when advisor is unavailable.

### Key entities (SmartBudgetApp)
`UserEntity` ↔ `AccountEntity` ↔ `TransactionEntity`, `BudgetEntity` (per-category), `CategoryEntity` (enum-backed: FOOD, TRANSPORT, ENTERTAINMENT, etc.), `CurrencyEntity` (enum-backed), `RoleEntity`.

### Testing
- SmartBudgetAdvisorApp: Unit tests (Mockito) and integration tests (`@SpringBootTest`) for service and controller layers
- SmartBudgetApp: Unit tests for `UserService`; uses H2 in-memory database for test profile
