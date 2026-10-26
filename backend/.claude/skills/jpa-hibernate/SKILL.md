---
name: jpa-hibernate
description: Implement and explain Spring Data JPA and Hibernate safely alongside SQL Server stored procedures.
---

# JPA and Hibernate skill

When implementing persistence:

1. Explain the distinction: JPA is the persistence specification, Hibernate is its implementation, Spring Data JPA provides repository abstractions.
2. Map entity fields exactly to the Flyway-managed SQL Server schema.
3. Use `EnumType.STRING` or an explicit converter; never ordinal values.
4. Use `BigDecimal` with an explicit precision/scale for prices.
5. Explain entity states, persistence context, dirty checking, flush, and commit in project documentation.
6. Keep `ddl-auto=validate`, `open-in-view=false`, and Flyway authoritative.
7. Use stored procedure calls for the required CRUD path. Do not assume derived query methods or `save()` invoke procedures.
8. Prefer JPA procedure APIs only when parameter and result mapping are clear; otherwise use Spring-managed JDBC in the same transaction.
9. Handle managed entity staleness if a procedure changes data behind Hibernate's persistence context.
10. Verify SQL Server procedure behavior with real SQL Server integration tests.
11. Do not introduce relationships unless the data model needs them.
12. Keep generated SQL logging configurable and avoid sensitive parameter logging.
