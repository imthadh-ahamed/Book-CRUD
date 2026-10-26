# Task 05 — Test coverage

## Required tests
- Domain/service unit tests
- Controller/API tests
- SQL Server integration tests using Testcontainers if supported
- Stored-procedure tests for each CRUD operation
- Missing ID and delete behavior
- Invalid/missing title, invalid stock enum, negative price, decimal precision
- Transaction rollback where multi-step work exists
- Flyway migration startup and Hibernate schema validation

## Acceptance criteria
- Test commands and actual results are recorded.
- H2 is not treated as proof of SQL Server procedure compatibility.
- Skipped tests are clearly identified.
