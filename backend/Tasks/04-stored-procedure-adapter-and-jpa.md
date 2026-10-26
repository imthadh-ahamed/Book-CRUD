# Task 04 — Stored procedures plus ORM

Create SQL Server CRUD procedures and a dedicated persistence adapter. Invoke procedures explicitly using JPA procedure APIs where appropriate or Spring-managed JDBC where more reliable. Add BookEntity and Spring Data JPA repository for meaningful ORM learning; document which active operations use procedures and which use Hibernate-generated SQL. Never claim `JpaRepository.save()` automatically invokes procedures. Handle flush/clear/refresh and transaction participation correctly. Add SQL Server integration tests.
