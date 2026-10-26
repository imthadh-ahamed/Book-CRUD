# Task 02 — SQL Server schema and stored procedures

## Goal
Create the books table and versioned SQL Server CRUD procedures using Flyway.

## Table
- `id BIGINT IDENTITY(1,1)` primary key
- `title NVARCHAR(...) NOT NULL`
- `description NVARCHAR(...) NULL`
- `stock NVARCHAR(...) NOT NULL`, constrained to `AVAILABLE`, `BOOKED`, `OUT_OF_STOCK`
- `is_shown BIT NOT NULL`
- `price DECIMAL(12,2) NOT NULL`, nonnegative check constraint
- Optional audit timestamps if implemented consistently

## Procedures
- `dbo.Book_Create`
- `dbo.Book_GetById`
- `dbo.Book_GetAll`
- `dbo.Book_Update`
- `dbo.Book_Delete`

Use `SET NOCOUNT ON`, parameters, and consistent result shapes. Decide how a missing ID and duplicate/constraint error are represented. Support deterministic ordering; use safe pagination if included.

## Acceptance criteria
- Flyway migrations create the table and procedures on SQL Server.
- No Hibernate schema generation.
- Stored procedures can be invoked manually using documented example calls.
- Add SQL Server integration tests or document why they are not runnable yet.
