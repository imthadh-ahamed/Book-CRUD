---
name: clean-architecture
description: Apply pragmatic Clean Architecture and SOLID to the Book CRUD backend.
---

# Clean Architecture skill

1. Read `CLAUDE.md` and `.claude/rules/architecture.md`.
2. Identify the use case, domain rules, and external boundaries before changing code.
3. Keep domain types independent of framework annotations where practical.
4. Keep web DTOs separate from JPA entities and domain models.
5. Put SQL Server and stored-procedure details in infrastructure adapters.
6. Keep application services responsible for orchestration and transaction boundaries.
7. Use ports where they protect the application from infrastructure details; avoid interfaces that add no value.
8. For each change, explain which SOLID principles are improved and what trade-off is introduced.
9. Add or update tests alongside the implementation.
