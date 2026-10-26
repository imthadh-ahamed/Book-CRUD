# Book CRUD API

A Dockerized REST API for managing books. Built as a Java learning project covering Spring Boot, JPA/Hibernate, SQL Server stored procedures, Flyway migrations, and OpenAPI documentation.

---

## Contents

1. [Project overview](#1-project-overview)
2. [Prerequisites](#2-prerequisites)
3. [Configuration](#3-configuration)
4. [Run locally](#4-run-locally)
5. [API documentation](#5-api-documentation)
6. [REST endpoints](#6-rest-endpoints)
7. [Architecture](#7-architecture)
8. [Stored procedures and migrations](#8-stored-procedures-and-migrations)
9. [JPA versus stored procedures](#9-jpa-versus-stored-procedures)
10. [Testing](#10-testing)
11. [Current test results](#11-current-test-results)
12. [Troubleshooting](#12-troubleshooting)
13. [Architecture decisions and limitations](#13-architecture-decisions-and-limitations)

---

## 1. Project overview

Manages a catalogue of books through a five-operation REST API. The application is intentionally multi-layered so each layer can be studied separately:

| Concern | Technology |
|---------|-----------|
| Language / platform | Java 21, Maven |
| Web framework | Spring Boot 3.4.5, Spring MVC |
| Database | Microsoft SQL Server 2022 |
| Schema migrations | Flyway |
| Active CRUD path | `JdbcTemplate` calling SQL Server stored procedures |
| Schema validation | Hibernate (`ddl-auto=validate`) |
| ORM learning path | Spring Data JPA (`SpringDataBookRepository`) |
| API documentation | Springdoc OpenAPI 2.8.9 / Swagger UI |
| Tests | JUnit 5, Mockito, Testcontainers |
| Container runtime | Docker, Docker Compose |

The project deliberately keeps two persistence paths in parallel so you can compare them:

- **Production path** — `BookController → BookService → BookRepository → JdbcTemplate → stored procedure → SQL Server`. This is what every API request uses.
- **Learning path** — `SpringDataBookRepository extends JpaRepository<BookEntity, Long>`. Not wired into any controller. Used only in `BookEntityJpaTest` to teach Hibernate entity lifecycle, dirty checking, and stale-state behavior.

---

## 2. Prerequisites

| Software | Required version | Notes |
|----------|-----------------|-------|
| Java (JDK) | 21 | Maven wrapper downloads Maven; only Java is needed |
| Docker Desktop | Any recent version | Required for SQL Server via Compose and Testcontainers |
| Git | Any | To clone the repository |

SQL Server runs inside Docker. You do not need a local SQL Server installation.

---

## 3. Configuration

The application reads all database credentials from environment variables. No password is hardcoded anywhere.

| Variable | Default | Required |
|----------|---------|---------|
| `DB_URL` | `jdbc:sqlserver://localhost:1433;databaseName=BookCrudDev;encrypt=true;trustServerCertificate=true` | Only if overriding |
| `DB_USERNAME` | `sa` | Only if overriding |
| `DB_PASSWORD` | *(none)* | **Yes** — app refuses to start if unset |

### Setup

```bash
cp .env.example .env
# Open .env and set DB_PASSWORD to a value meeting SQL Server complexity rules:
#   at least 8 characters, mixing uppercase, lowercase, digits, and symbols
#   example shape: MyStr0ng!Pass
```

`.env` is listed in `.gitignore` and must never be committed. `.env.example` contains only placeholder values — inspect it to see the expected format.

---

## 4. Run locally

### Step 1 — Start SQL Server

```bash
docker compose up -d sqlserver sqlserver-init
```

This starts the SQL Server container and runs a one-time initializer that creates the `BookCrudDev` database. The initializer exits with code 0 when done. SQL Server data is stored in the `sqlserver_data` Docker volume and survives container restarts.

Wait for the container to be healthy before proceeding. You can check with:

```bash
docker compose ps
```

The `sqlserver` service should show `(healthy)` after roughly 30–60 seconds.

### Step 2 — Start the application

```bash
./mvnw spring-boot:run
```

On first start, Flyway applies the three versioned migrations in order:

- `V1__create_books_table.sql` — creates `dbo.books`
- `V2__create_book_procedures.sql` — creates the five CRUD stored procedures
- `V3__create_book_functions.sql` — creates `dbo.fn_book_stock_label`

Hibernate then reads the live schema and validates it against `BookEntity`. If the schema matches, the application starts. If a column is missing or mistyped, startup fails with a clear error.

### Full Docker stack

```bash
docker compose --profile app up --build
```

This builds a two-stage Docker image (JDK builder → JRE runtime) and starts the full stack. The build context is the **repository root** (one level above `backend/`), because the Maven resource plugin copies Flyway migrations from `../database/migrations` and that path must be accessible to the Docker build.

> **Port conflict:** If port 8080 is already bound by a locally running Spring Boot process, `docker compose up` will fail. Stop the local process first, or change the host-side port in `compose.yaml` (`"8081:8080"`).

The container connects to SQL Server using the Compose service hostname (`sqlserver:1433`), not `localhost`. The `DB_URL` environment variable in `compose.yaml` is already set to `jdbc:sqlserver://sqlserver:1433/...`.

---

## 5. API documentation

Once the application is running:

| URL | Content |
|-----|---------|
| `http://localhost:8080/swagger-ui/index.html` | Interactive Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |

> **Verified on 2026-10-27** — both URLs returned `200 OK` when the application ran inside the Docker container during Phase C. The OpenAPI document was confirmed to describe all five `/api/books` endpoints.

---

## 6. REST endpoints

All endpoints are under `/api/books`. Request and response bodies are JSON. Errors follow [RFC 7807 ProblemDetail](https://www.rfc-editor.org/rfc/rfc7807).

### `POST /api/books` — Create a book

**Request body:**

```json
{
  "title": "Clean Code",
  "description": "A handbook of agile software craftsmanship",
  "stock": "AVAILABLE",
  "isShown": true,
  "price": 29.99
}
```

`description` is optional. `stock` must be one of `AVAILABLE`, `BOOKED`, or `OUT_OF_STOCK`. `price` must be `>= 0` with at most two decimal places.

**Response `201 Created`:**

```json
{
  "id": 1,
  "title": "Clean Code",
  "description": "A handbook of agile software craftsmanship",
  "stock": "AVAILABLE",
  "isShown": true,
  "price": 29.99
}
```

**Validation failure `400 Bad Request`:**

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "..."
}
```

---

### `GET /api/books/{id}` — Get one book

```
GET /api/books/1
```

**Response `200 OK`:** full book object (same shape as POST response).

**Not found `404 Not Found`:**

```json
{
  "type": "about:blank",
  "title": "Book Not Found",
  "status": 404,
  "detail": "Book not found with id: 1"
}
```

---

### `GET /api/books` — List all books

```
GET /api/books
```

**Response `200 OK`:** JSON array of book objects, ordered by `id ASC`. Returns an empty array when no books exist.

---

### `PUT /api/books/{id}` — Update a book

**Request body:** same fields as POST, all required. Full replacement — no partial updates.

**Response `200 OK`:** updated book object.

**Not found `404 Not Found`:** same shape as GET 404.

---

### `DELETE /api/books/{id}` — Delete a book

```
DELETE /api/books/1
```

**Response `204 No Content`:** no body.

**Not found `404 Not Found`:** same shape as GET 404.

---

## 7. Architecture

```
HTTP request
    │
    ▼
BookController          ← handles HTTP only (status codes, request parsing, response shape)
    │
    ▼
BookService             ← @Transactional; throws BookNotFoundException; no SQL here
    │
    ▼
BookRepository          ← @Repository; JdbcTemplate; EXEC stored procedures; RowMapper
    │
    ▼
SQL Server              ← stored procedures own the SQL logic; Flyway owns the schema
```

Package layout under `com.bookCRUD.backend`:

```
BackendApplication.java
controller/
  BookController.java
service/
  BookService.java
repository/
  BookRepository.java          ← active CRUD path (stored procedures via JdbcTemplate)
  SpringDataBookRepository.java← ORM learning only — not wired into BookService
entity/
  BookEntity.java              ← @Entity used by Hibernate for ddl-auto=validate
  StockStatus.java
dto/
  CreateBookRequest.java
  UpdateBookRequest.java
  BookResponse.java
exception/
  BookNotFoundException.java
  ApiExceptionHandler.java
```

**SOLID in practice:**

- `BookController` handles HTTP only; it never touches SQL or business rules.
- `BookService` handles the "what" (create, find, update, delete) and owns `@Transactional` boundaries.
- `BookRepository` handles the "how" (which stored procedure, how to map the result set).
- Each layer depends on the one below via constructor injection.

---

## 8. Stored procedures and migrations

### Five procedures

| Procedure | Operation | Key behavior |
|-----------|-----------|-------------|
| `dbo.Book_Create` | INSERT | Returns the inserted row via `SCOPE_IDENTITY()`; `BookRepository` throws `IllegalStateException` if the procedure returns no rows |
| `dbo.Book_GetById` | SELECT one | Returns 0 rows when id is not found; caught as `Optional.empty()` |
| `dbo.Book_GetAll` | SELECT all | Returns rows ordered by `id ASC` |
| `dbo.Book_Update` | UPDATE | Returns 0 rows when id is not found; caught as `Optional.empty()` |
| `dbo.Book_Delete` | DELETE | Returns a `rows_affected` scalar; `0` means the id did not exist |

All procedures use `SET NOCOUNT ON` and named parameters. No string-concatenated SQL.

### Why JdbcTemplate instead of JPA stored-procedure APIs

JPA's `@NamedStoredProcedureQuery` maps output parameters well, but it cannot cleanly map a procedure that returns a `SELECT` result set as a managed entity — the column aliases and entity mapping must match exactly, and SQL Server result sets from procedures do not always satisfy that contract. `JdbcTemplate.query()` with an explicit `RowMapper` maps each column by name, which is clear, testable, and independent of the entity graph. See `BookRepository.BOOK_ROW_MAPPER`.

### Flyway migration authority

Flyway is the only thing that creates or alters the database schema. Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate` — it reads the live schema and verifies it, but never writes to it.

Migration SQL files live in `database/migrations/` at the repository root. The Maven resource plugin in `pom.xml` copies them onto the classpath at `db/migration` during the build, which is the path Flyway scans.

Reference copies of the same SQL also live in `database/tables/`, `database/procedures/`, and `database/functions/` for human reading. These are kept manually in sync with the migrations. See [`../database/README.md`](../database/README.md) for the database folder structure.

---

## 9. JPA versus stored procedures

### The two paths

| | Production path | Learning path |
|---|---|---|
| Class | `BookRepository` | `SpringDataBookRepository` |
| Mechanism | `JdbcTemplate` + stored procedures | `JpaRepository<BookEntity, Long>` |
| Wired into `BookService`? | **Yes** | **No** |
| Runs on every API request? | **Yes** | **No** |
| Tested in | `BookRepositoryIntegrationTest` | `BookEntityJpaTest` |

`SpringDataBookRepository` is a real, working Spring Data JPA repository — it compiles, loads, and its tests pass against a real SQL Server container. It is not wired into any controller or service, so it has no effect on API behavior.

### JPA, Hibernate, and Spring Data JPA — what each one is

**JPA (Jakarta Persistence API)** is a specification. It defines annotations like `@Entity`, `@Column`, and `@Id`, and the `EntityManager` interface. JPA contains no implementation code — it is a contract.

**Hibernate** is the most widely used JPA implementation. It reads your `@Entity` annotations and generates SQL. When you call `entityManager.persist(entity)`, Hibernate decides what `INSERT` to run and when to run it.

**Spring Data JPA** sits on top of Hibernate. It reads repository interface method names (`findByStock` → `SELECT ... WHERE stock = ?`) and `@Query` annotations and generates a complete class at startup. You write an interface; Spring Data writes the class. The generated class delegates to an `EntityManager` backed by Hibernate.

### Entity lifecycle states

| State | How to reach it | What Hibernate does |
|-------|----------------|---------------------|
| **Transient** | `new BookEntity(...)` | Knows nothing about it; no row, no tracking |
| **Managed** | After `save()`, or returned by `findById()` | Snapshots all fields; flushes changes on commit |
| **Detached** | After `em.detach()`, `em.clear()`, or transaction end | Stops tracking; mutations are silently ignored |
| **Removed** | After `em.remove()` | Marks for DELETE on next flush |

### Dirty checking

Hibernate snapshots each managed entity's fields when it enters the persistence context. On flush, it compares current values against the snapshot. Any changed field triggers an automatic `UPDATE` — you do not call `save()` again.

```java
// Managed entity — already in persistence context
BookEntity book = jpaRepo.findById(id).orElseThrow();
book.setTitle("New Title"); // no save() call
em.flush();                  // Hibernate issues UPDATE dbo.books SET title=? WHERE id=?
```

### Stale state after stored-procedure updates

Stored procedures bypass the persistence context. If Hibernate has entity id=5 in its first-level cache and then a `JdbcTemplate` call runs `EXEC dbo.Book_Update` for id=5, Hibernate's cache still holds the old values. The next `findById(5)` returns cached (stale) data.

Fix: call `em.clear()` to evict all entities from the cache before re-reading.

```java
// After a procedure update, clear the cache before re-reading via JPA
em.clear();
BookEntity fresh = jpaRepo.findById(id).orElseThrow(); // hits DB, not cache
```

`BookEntityJpaTest.staleState_procedureBypassesPersistenceContext` demonstrates this exact scenario with assertions on both the stale and the fresh value.

### Why `ddl-auto=validate` and Flyway together

`ddl-auto=validate` means Hibernate reads the live schema and checks that every `@Entity` field matches a column. If the schema drifts from the entity mapping (e.g., a migration adds a `NOT NULL` column that `BookEntity` does not declare), the application fails at startup with a clear message. Flyway owns schema creation and changes; Hibernate owns schema verification. The two complement each other.

---

## 10. Testing

### Unit tests (no database, no Docker required)

```bash
./mvnw test
```

Runs all tests **except** those tagged `integration`. Covers:

- `BookControllerTest` — 10 MockMvc tests for all five endpoints, validation, and 404 behavior
- `BookServiceTest` — 9 Mockito tests for service logic and exception cases
- `BookRepositoryTest` — 1 Mockito test for the `IllegalStateException` when `Book_Create` returns no rows
- `BackendApplicationTests` — 1 Spring context load test (DataSource mocked)

### Procedure integration tests (Docker required)

Starts a SQL Server container via Testcontainers, applies Flyway migrations, and runs all five stored procedures.

```bash
# Temporarily comment out <excludedGroups>integration</excludedGroups> in pom.xml,
# then run:
./mvnw test -Dtest=BookRepositoryIntegrationTest
```

> **Note on Surefire configuration:** `pom.xml` excludes the `integration` tag by default so that `mvnw test` stays fast. The command-line property `-DexcludedGroups=` does not reliably override a pom.xml `<excludedGroups>` value in Maven Surefire 3.5.3. To run integration tests, temporarily comment out the `<excludedGroups>integration</excludedGroups>` line in `pom.xml`, run the tests, then restore it.

Covers: create with generated ID, nullable description, distinct IDs, findById (found/not found), findAll, update (all fields/not found), delete (found/not found), decimal precision, all three `StockStatus` values.

### JPA integration tests (Docker required)

Same Testcontainers approach. Uses `SpringDataBookRepository` and `EntityManager` directly.

```bash
# Same Surefire override as above, then:
./mvnw test -Dtest=BookEntityJpaTest
```

Covers: `save()` and IDENTITY assignment, dirty checking triggering automatic UPDATE, detached entity mutation ignored, stale state after stored-procedure update and `em.clear()` fix, `findByStock` derived query, `findAffordable` JPQL query.

### Testcontainers version note

The Spring Boot 3.4.5 BOM manages Testcontainers at 1.20.6. This project overrides it to **1.21.4** (`<testcontainers.version>1.21.4</testcontainers.version>` in `pom.xml`) because 1.20.6 sends a raw `GET /info` HTTP request to Docker Desktop's Windows named-pipe proxy, which returns HTTP 400 in Docker Desktop 4.x. Testcontainers 1.21.4 changed its Windows Docker client detection strategy and avoids this probe.

---

## 11. Current test results

These results were recorded on 2026-10-27 during development. They are the last-reported results, not a live status.

| Suite | Command | Tests | Result |
|-------|---------|-------|--------|
| Unit tests | `./mvnw test` | 21 | **PASS** |
| Procedure integration | `BookRepositoryIntegrationTest` | 12 | **PASS** |
| JPA integration | `BookEntityJpaTest` | 6 | **PASS** |
| **Total** | | **39** | **PASS** |

---

## 12. Troubleshooting

### SQL Server container is not healthy

Check the Docker logs:

```bash
docker compose logs sqlserver
```

SQL Server takes 30–60 seconds to initialize. The healthcheck runs `SELECT 1` via `sqlcmd` every 10 seconds with up to 12 retries. If it never becomes healthy, the most common causes are:

- `DB_PASSWORD` does not meet SQL Server complexity requirements (upper + lower + digit + symbol, at least 8 characters)
- Port 1433 is already bound by a local SQL Server installation

### Application fails to start — connection refused

Ensure `.env` exists and `DB_PASSWORD` is set. The application has no default for `DB_PASSWORD` and will throw `IllegalStateException` on startup if it is absent.

If connecting to the Dockerized SQL Server from a local `mvnw spring-boot:run`, confirm that the `sqlserver` container is healthy and that `DB_URL` points to `localhost:1433` (not `sqlserver:1433`, which is the Docker internal hostname used by other containers).

### Flyway checksum error

If you edited a migration file after it was applied, Flyway will refuse to start with a checksum mismatch. Fix options:

1. Revert the migration file to its original content (preferred).
2. Delete the row from `dbo.flyway_schema_history` for that version and re-apply (development only).

Never edit an already-applied migration in shared or production environments.

### Hibernate validation failure at startup

```
Schema-validation: missing column [...]
```

This means `BookEntity` declares a field that the database table does not have. Check that Flyway applied all migrations. Re-running the application will attempt to re-apply any missing migrations.

### Testcontainers: Docker Desktop 4.x HTTP 400 error

If you see:

```
BadRequestException (Status 400: {"ID":"","Containers":0,...})
```

Ensure `<testcontainers.version>1.21.4</testcontainers.version>` is present in `pom.xml`. This is already set. If it is missing (e.g., after a merge), add it back to the `<properties>` block.

### Swagger UI not accessible

Confirm the application started successfully (no errors in the console). Springdoc publishes Swagger UI at the default path only when the `springdoc-openapi-starter-webmvc-ui` dependency is on the classpath, which it is (version 2.8.9 in `pom.xml`). If the port is different, check `server.port` in `application.properties` (none is set, so 8080 is used).

---

## 13. Architecture decisions and limitations

### Why JdbcTemplate for stored procedures, not JPA stored-procedure APIs

JPA's `@NamedStoredProcedureQuery` maps output parameters but struggles with procedures that return a `SELECT` result set — column aliases must exactly match entity field names, and Hibernate may attempt to register the result as a managed entity. `JdbcTemplate.query()` with an explicit `RowMapper` maps each column by name, has no entity lifecycle implications, and fails clearly if a column is absent. For these five procedures, JdbcTemplate is the more predictable tool.

### Why layered architecture instead of Clean Architecture (Hexagonal)

The project was refactored from Clean Architecture (ports/adapters) to a traditional layered architecture (`Controller → Service → Repository`). The layered model is easier to follow when first learning Spring Boot because the class names (`BookService`, `BookRepository`) directly correspond to their Spring stereotypes, and there are no additional indirection layers (ports, adapters, use-case interfaces) to navigate before understanding what a request does.

### Why `SpringDataBookRepository` is not the active CRUD path

CLAUDE.md explicitly requires stored procedures for all CRUD operations, and also requires JPA/Hibernate to be a genuine part of the architecture. Both requirements are satisfied without compromise: procedures handle all production requests; `SpringDataBookRepository` demonstrates ORM concepts in tests without a false claim that `JpaRepository.save()` calls a stored procedure.

### Known limitations and items outside current scope

| Item | Status |
|------|--------|
| Multi-stage `Dockerfile` | Implemented (`backend/Dockerfile`). Build context is the repository root; see §4. |
| OpenAPI response documentation | `@ApiResponse`, `@Schema`, and `@Parameter` annotations are not yet added. Swagger UI shows the endpoints but not the 400/404 response shapes or field descriptions. |
| Pagination and filtering | `GET /api/books` returns all rows. No page/sort parameters. `dbo.Book_GetAll` orders by `id ASC`. |
| Authentication | None. This API is not intended for public deployment without an auth layer. |
| Audit timestamps (`createdAt`, `updatedAt`) | Not implemented. The schema and entity do not include them. |
| Transaction rollback test | No multi-step operation currently exists, so there is nothing to roll back across multiple procedure calls in a single service method. |
| Hibernate schema-validation failure test | No test verifies the fast-fail behavior when a column is missing. |
