# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 order management service API using:
- Java 17
- Maven as build tool
- Spring Boot Web for REST API
- Spring Data JPA with PostgreSQL
- Spring Data Redis for caching
- MapStruct for object mapping
- Spotify fmt-maven-plugin for code formatting

## Common Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

# Package the application
./mvnw clean package
```

### Code Quality
```bash
# Format code (automatic via fmt-maven-plugin)
./mvnw fmt:format

# Check code formatting
./mvnw fmt:check
```

## Architecture

### Package Structure
- `controllers/` - REST API endpoints with DTOs in sub-packages
- `services/` - Business logic interfaces with implementations in `impl/`
- `repositories/` - Data access layer with JPA entities in `entities/`
- `exceptions/` - Custom exceptions and global exception handler
- `services/mappers/` - MapStruct mappers for entity-DTO conversion

### Key Patterns
- **Dependency Injection**: Constructor injection used throughout
- **Service Layer**: Interface-based services with separate implementations
- **Repository Pattern**: JPA repositories extending `JpaRepository`
- **DTO Pattern**: Separate request/response DTOs in controller layer
- **Exception Handling**: Global exception handler with custom exceptions
- **Caching**: Redis caching enabled at application level

### Entity Relationships
- `Product` entity with `@ManyToOne` relationship to `Manufacturer`
- JPA entities use standard getters/setters without constructors
- BigDecimal used for monetary values

### Technology Stack Integration
- **MapStruct**: Used for entity-to-DTO mapping with annotation processors
- **Spring Cache**: Caching abstraction enabled with `@EnableCaching`
- **Spring Actuator**: Health checks and monitoring endpoints
- **PostgreSQL**: Primary database
- **Redis**: Caching layer

## Development Notes

- Main application class enables caching with `@EnableCaching`
- Repository interfaces are minimal, extending `JpaRepository`
- Service implementations use functional programming style (e.g., `Optional.map()`)
- Custom exceptions follow factory pattern (e.g., `NotFoundException.ProductNotFound()`)
- Code formatting enforced automatically via maven plugin