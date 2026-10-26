# Book CRUD Backend — Claude Code Project Kit

This ZIP is a **complete requirements and workflow kit for Claude Code**, not the generated Java application. It tells Claude how to build a Dockerized Book CRUD REST API with OpenAPI/Swagger documentation and a separately organized SQL Server database folder.

## Stack

- Java 21, Maven, Spring Boot 3.x
- Spring Web, Validation, Spring Data JPA, Hibernate
- Microsoft SQL Server (not PostgreSQL)
- Flyway database migrations
- Stored procedures for CRUD operations
- Springdoc OpenAPI / Swagger UI
- JUnit 5, Spring Boot Test, Testcontainers where available
- Docker and Docker Compose

## Included

- `CLAUDE.md`
- `.mcp.json`
- `.claude/agents/`
- `.claude/skills/`
- `.claude/commands/`
- `.claude/rules/`
- `Tasks/`
- A required `database/` folder structure with migrations, tables, procedures, functions, scripts, and documentation.

## Use

1. Extract the ZIP into the root of a new repository.
2. Open that root folder in Claude Code.
3. Ask Claude to read `CLAUDE.md`, all relevant rules/skills, and the `Tasks/` files.
4. Start with `Tasks/00-implementation-plan.md`; do not write code until a plan is presented and reviewed.
5. Implement one task at a time, running the relevant tests and reviewing the diff after each phase.

Example initial prompt:

> Read `CLAUDE.md`, `.claude/rules/`, `.claude/skills/`, `.claude/agents/`, and `Tasks/`. Inspect the workspace. Do not write application code yet. Return a phased implementation plan, including the database-folder/Flyway strategy, stored-procedure contracts, Swagger/OpenAPI configuration, Docker setup, and test plan.

## Database assumption

“SQL, not PostgreSQL” is interpreted as **Microsoft SQL Server** because the requirements explicitly call for stored procedures and a Dockerized database. If you meant MySQL or MariaDB, change the database engine requirements consistently before implementation.
