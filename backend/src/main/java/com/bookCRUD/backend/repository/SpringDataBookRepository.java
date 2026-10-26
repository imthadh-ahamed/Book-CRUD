package com.bookCRUD.backend.repository;

import com.bookCRUD.backend.entity.BookEntity;
import com.bookCRUD.backend.entity.StockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * ORM learning repository — demonstrates Spring Data JPA and Hibernate behavior.
 *
 * THIS IS NOT THE ACTIVE CRUD PATH. All five CRUD API endpoints (POST/GET/PUT/DELETE
 * /api/books) route through BookRepository (JdbcTemplate + stored procedures).
 * This interface exists to show how Hibernate and Spring Data JPA work, so the
 * architecture teaches both persistence strategies without conflating them.
 *
 * ── JPA vs Hibernate vs Spring Data JPA ──────────────────────────────────────
 *
 * JPA (Jakarta Persistence API) is a specification — a set of annotations
 * (@Entity, @Column, @Id) and interfaces (EntityManager). It contains no
 * implementation code; it is a contract that vendors implement.
 *
 * Hibernate is the most widely used JPA implementation. It reads your @Entity
 * annotations and generates database-specific SQL at runtime. When you annotate
 * a class with @Entity and @Column, Hibernate translates those into
 * CREATE/SELECT/INSERT/UPDATE/DELETE statements appropriate for your database.
 *
 * Spring Data JPA sits on top of Hibernate. It reads repository interface
 * method names (findByStock → SELECT ... WHERE stock = ?) and @Query
 * annotations and generates a complete implementation at startup. You write
 * an interface; Spring Data writes the class. That class delegates to an
 * EntityManager backed by Hibernate.
 *
 * ── What happens on jpaRepo.save(entity)? ────────────────────────────────────
 *
 * 1. Spring Data JPA calls entityManager.persist(entity) for a new entity.
 * 2. Hibernate adds the entity to the first-level cache (persistence context).
 *    It does NOT immediately run an INSERT.
 * 3. When the transaction commits, or em.flush() is called, Hibernate checks
 *    every managed entity for changed fields (dirty checking) and issues SQL.
 * 4. For a new entity the INSERT runs and the IDENTITY value is read back into
 *    the Java object via SCOPE_IDENTITY() or an equivalent mechanism.
 *
 * ── Entity lifecycle states ───────────────────────────────────────────────────
 *
 * Transient — new BookEntity(...) — not associated with any EntityManager;
 *             Hibernate knows nothing about it; no row exists yet.
 *
 * Managed   — after em.persist() / jpaRepo.save(), or returned by em.find() /
 *             jpaRepo.findById() — the entity is in the first-level cache;
 *             Hibernate snapshots its fields and will flush changes on commit.
 *
 * Detached  — after em.detach(entity), em.clear(), or after the transaction
 *             ends — the Java object still exists in heap memory but Hibernate
 *             no longer tracks it; mutations have no effect on the database.
 *
 * Removed   — after em.remove(entity) — marked for DELETE on the next flush.
 *
 * ── Dirty checking ────────────────────────────────────────────────────────────
 *
 * Hibernate snapshots each managed entity on entry. On flush it compares
 * current field values against the snapshot. Changed fields → automatic UPDATE.
 * You never call save() again to update; just mutate the managed entity.
 * See BookEntityJpaTest.dirtyCheck_automaticallyUpdatesOnFlush.
 *
 * ── Stale state after stored procedures ──────────────────────────────────────
 *
 * Stored procedures bypass the persistence context. If you load BookEntity id=5
 * (it is now in the first-level cache) and then call dbo.Book_Update via
 * JdbcTemplate, Hibernate's cache still holds the old values. The next
 * jpaRepo.findById(5) hits the cache, not the database, and returns stale data.
 * Fix: call em.clear() to evict all entities, or em.refresh(entity) for one.
 * See BookEntityJpaTest.staleState_procedureBypassesPersistenceContext.
 *
 * ── EnumType.STRING ───────────────────────────────────────────────────────────
 *
 * BookEntity maps StockStatus with @Enumerated(EnumType.STRING). Hibernate
 * stores the enum constant name ("AVAILABLE", "BOOKED", "OUT_OF_STOCK") as a
 * VARCHAR/NVARCHAR column value. The alternative, EnumType.ORDINAL, stores the
 * enum's ordinal integer (0, 1, 2). ORDINAL is fragile: reordering enum
 * constants silently corrupts existing rows. Always use STRING for enums that
 * are persisted to a database.
 */
public interface SpringDataBookRepository extends JpaRepository<BookEntity, Long> {

    /**
     * Spring Data derives the query from the method name.
     * Generated JPQL: SELECT b FROM BookEntity b WHERE b.stock = ?1
     * Translated SQL: SELECT ... FROM dbo.books WHERE stock = ?
     */
    List<BookEntity> findByStock(StockStatus stock);

    /**
     * Derived query on a boolean field.
     * Generated JPQL: SELECT b FROM BookEntity b WHERE b.isShown = true
     */
    List<BookEntity> findByIsShownTrue();

    /**
     * Explicit JPQL query using @Query. JPQL addresses entity class and field
     * names (BookEntity, price), not table/column names (dbo.books, price).
     * The equivalent T-SQL would be:
     *   SELECT * FROM dbo.books WHERE price <= @maxPrice ORDER BY price ASC
     */
    @Query("SELECT b FROM BookEntity b WHERE b.price <= :maxPrice ORDER BY b.price ASC")
    List<BookEntity> findAffordable(@Param("maxPrice") BigDecimal maxPrice);
}
