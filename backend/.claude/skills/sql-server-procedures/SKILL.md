---
name: sql-server-procedures
description: Design SQL Server stored procedures and connect them safely to Spring Boot persistence adapters.
---

# SQL Server stored procedures skill

- Use T-SQL compatible with the selected SQL Server container version.
- Use schema-qualified names such as `dbo.Book_Create`.
- Use strongly typed input parameters, `SET NOCOUNT ON`, and deterministic result shapes.
- Define missing-row and constraint-error behavior for every procedure.
- Use result sets or output parameters consistently and document them.
- Avoid dynamic SQL; if it is unavoidable, parameterize it safely.
- Use `NVARCHAR`, `BIT`, `DECIMAL`, and `BIGINT` appropriately.
- Use Flyway versioned migrations to create/alter procedures.
- Keep procedure invocations inside a persistence adapter.
- Test procedure calls against SQL Server, not only H2.
- Document when `EntityManager` is used and when JDBC is chosen.
