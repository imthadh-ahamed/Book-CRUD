# Testing rules

- Test behavior, not private implementation details.
- Unit-test domain rules and application services.
- Test controllers and validation behavior.
- Test actual SQL Server stored procedures against SQL Server, preferably using Testcontainers.
- H2 is not a substitute for verifying SQL Server-specific T-SQL, result sets, output parameters, or stored-procedure behavior.
- Test create/read/list/update/delete, missing IDs, invalid stock values, invalid prices, and constraints.
- Test transaction rollback if any operation has multiple database writes.
- Never claim tests passed unless the command was actually executed.
- If Docker or SQL Server is unavailable, clearly report skipped integration tests.
