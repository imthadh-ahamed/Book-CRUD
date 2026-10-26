# Task 03 — Domain, DTOs, validation, and REST API

## Goal
Create the Book domain model, stock enum, use cases/services, DTOs, controller, and exception handling.

## Endpoints
- `POST /api/books`
- `GET /api/books/{id}`
- `GET /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

## Rules
- Use DTOs; never serialize JPA entities directly.
- Validate title, price, stock, and request shape.
- Use `BigDecimal` for price.
- Return consistent errors and correct HTTP status codes.
- Keep controllers thin and application services focused.

## Acceptance criteria
- OpenAPI documents every endpoint and response.
- Unit/API tests cover success and validation/error cases.
