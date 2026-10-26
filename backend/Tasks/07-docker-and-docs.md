# Task 06 — Dockerize and document

## Goal
Deliver a repeatable local development experience.

## Work
- Multi-stage Dockerfile
- Compose file with app and SQL Server
- Persistent database volume
- Health/readiness checks and application retry behavior
- Environment-based credentials
- SQL Server EULA/license acknowledgement documented
- README with prerequisites, run commands, Swagger URL, endpoint examples, migrations, architecture, and troubleshooting
- ORM learning notes explaining JPA, Hibernate, Spring Data JPA, stored procedures, and transaction boundaries

## Acceptance criteria
- Document `docker compose up --build` workflow.
- No real secrets in tracked files.
- App waits for database readiness robustly.
- Swagger UI and CRUD endpoints are documented.
- Limitations and security caveats are explicit.
