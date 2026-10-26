# Architecture rules

- Follow the dependency direction and package layout in `CLAUDE.md`.
- Keep domain logic independent of Spring, Hibernate, SQL Server, and HTTP.
- Controllers translate HTTP requests/responses; application services orchestrate use cases; adapters perform I/O.
- Use request/response DTOs. Never return JPA entities directly from REST controllers.
- Keep stored-procedure calls behind a persistence adapter/port.
- Do not create abstraction layers without a clear boundary or benefit.
- Preserve a single source of truth for each business rule.
- Document any architecture deviation before implementing it.
