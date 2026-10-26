# Database assets

This folder organizes SQL Server scripts and migrations.

- `migrations/`: authoritative Flyway deployment history.
- `tables/`: reference definitions for tables.
- `procedures/`: reference definitions for stored procedures.
- `functions/`: reusable SQL functions only when justified.
- `scripts/`: development seed data and guarded local reset utilities.

## Migration source of truth

Flyway migrations are authoritative. Reference scripts must stay synchronized by a documented, repeatable process; do not edit the same definition independently in two places without parity checks. The backend build must package/copy the migrations into a runtime classpath location Flyway scans. Document the chosen implementation in the backend README and build configuration.

Use SQL Server T-SQL only. Do not put credentials in SQL files. Do not run reset scripts against shared or production databases.
