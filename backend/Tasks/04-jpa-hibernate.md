# Task 04 — JPA/Hibernate mapping and Spring Data JPA

## Goal
Make JPA/Hibernate a real part of the architecture without bypassing the stored-procedure CRUD requirement.

## Work
- Create `BookEntity` matching the Flyway table.
- Create a `SpringDataBookRepository extends JpaRepository<BookEntity, Long>` for appropriate ORM learning/query needs.
- Create a persistence adapter boundary.
- Decide and document which operations use stored procedures and which use ORM-generated SQL. Default active CRUD path must call the stored procedures as required.
- Use `EntityManager` procedure APIs when result mapping is reliable; use Spring-managed JDBC for procedures whose result contracts are better handled that way.
- Document entity states, persistence context, dirty checking, flush, commit, and stale state after procedures.
- Use `EnumType.STRING` if direct enum mapping is used.

## Acceptance criteria
- Hibernate validates schema; it never creates or alters it.
- No fake claim that `JpaRepository.save()` calls a procedure.
- CRUD API calls use the documented active path.
- Persistence-context synchronization is addressed and tested.
- Tests verify procedure result mapping and transactional behavior against SQL Server.
