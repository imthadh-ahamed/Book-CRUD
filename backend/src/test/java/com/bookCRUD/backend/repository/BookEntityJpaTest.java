package com.bookCRUD.backend.repository;

import com.bookCRUD.backend.entity.BookEntity;
import com.bookCRUD.backend.entity.StockStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ORM learning tests — demonstrates Hibernate and Spring Data JPA behavior
 * against a real SQL Server instance.
 *
 * All test methods are @Transactional (via the class annotation). Spring rolls
 * back each test's transaction automatically, so no data is left behind and no
 * cleanup code is needed.
 *
 * These tests use SpringDataBookRepository (the ORM learning path).
 * The production CRUD path (BookRepository + stored procedures) is tested in
 * BookRepositoryIntegrationTest.
 */
@Tag("integration")
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
@Transactional
class BookEntityJpaTest {

    @Container
    @ServiceConnection
    static MSSQLServerContainer<?> sqlServer =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                    .acceptLicense();

    @Autowired
    SpringDataBookRepository jpaRepo;

    @Autowired
    BookRepository procedureRepo; // used only in the stale-state lesson

    @PersistenceContext
    EntityManager em;

    private BookEntity newBook(String title) {
        return new BookEntity(title, null, StockStatus.AVAILABLE, true, new BigDecimal("9.99"));
    }

    // ── Lesson 1: save() and generated identity ───────────────────────────────
    //
    // new BookEntity() is in Transient state — no EntityManager knows about it.
    // save() calls em.persist(), moving the entity to Managed state.
    // Hibernate queues an INSERT; it does not execute immediately.
    // em.flush() forces Hibernate to send the queued INSERT to SQL Server.
    // SQL Server assigns the IDENTITY value and Hibernate reads it back.

    @Test
    void save_persistsEntityAndAssignsGeneratedId() {
        BookEntity transient_ = newBook("Learning JPA");
        assertThat(transient_.getId()).isNull(); // no ID until persisted

        BookEntity managed = jpaRepo.save(transient_);
        em.flush(); // INSERT runs here; SQL Server returns the new IDENTITY value

        assertThat(managed.getId()).isNotNull().isPositive();
    }

    // ── Lesson 2: dirty checking ──────────────────────────────────────────────
    //
    // Hibernate snapshots each managed entity on entry to the persistence context.
    // On flush, it compares current field values against the snapshot.
    // Changed fields → Hibernate issues UPDATE automatically. No save() needed.

    @Test
    void dirtyCheck_automaticallyUpdatesOnFlush() {
        BookEntity saved = jpaRepo.save(newBook("Original Title"));
        em.flush();
        Long id = saved.getId();

        // findById returns the managed entity (from cache or DB — still managed)
        BookEntity managed = jpaRepo.findById(id).orElseThrow();

        // Mutate the managed entity. No save() call.
        managed.setTitle("Updated by Dirty Check");

        // flush() triggers dirty-check:
        //   snapshot: "Original Title"
        //   current:  "Updated by Dirty Check"
        //   result:   UPDATE dbo.books SET title=? WHERE id=?
        em.flush();

        // Clear cache and reload from DB to confirm the UPDATE reached SQL Server
        em.clear();
        assertThat(jpaRepo.findById(id).orElseThrow().getTitle())
                .isEqualTo("Updated by Dirty Check");
    }

    // ── Lesson 3: detached entities ───────────────────────────────────────────
    //
    // em.detach() removes an entity from the persistence context.
    // Hibernate stops tracking it — mutations are silently ignored.

    @Test
    void detachedEntity_modificationDoesNotReachDatabase() {
        BookEntity saved = jpaRepo.save(newBook("Will Be Detached"));
        em.flush();
        Long id = saved.getId();

        em.detach(saved); // entity is now Detached — no longer managed

        saved.setTitle("Change on Detached"); // mutating detached entity

        em.flush(); // nothing in context to flush — no UPDATE is issued

        em.clear();
        assertThat(jpaRepo.findById(id).orElseThrow().getTitle())
                .isEqualTo("Will Be Detached"); // DB row is unchanged
    }

    // ── Lesson 4: stale state after a stored procedure ────────────────────────
    //
    // Stored procedures bypass the persistence context. Hibernate's first-level
    // cache does not learn about changes the procedure makes to the same row.
    // The next findById() hits the cache and returns stale data.
    // Fix: em.clear() evicts all cached entities; the next load goes to the DB.

    @Test
    void staleState_procedureBypassesPersistenceContext() {
        BookEntity entity = jpaRepo.save(newBook("Original via JPA"));
        em.flush();
        Long id = entity.getId();

        // Warm the first-level cache
        BookEntity cached = jpaRepo.findById(id).orElseThrow();
        assertThat(cached.getTitle()).isEqualTo("Original via JPA");

        // Update the same row via stored procedure — Hibernate does not see this
        procedureRepo.update(id, "Changed by Procedure", null,
                StockStatus.AVAILABLE, true, new BigDecimal("9.99"));

        // WITHOUT evicting: findById returns stale data from the first-level cache
        assertThat(jpaRepo.findById(id).orElseThrow().getTitle())
                .as("stale: first-level cache masks the procedure's update")
                .isEqualTo("Original via JPA");

        // Evict all managed entities from the persistence context
        em.clear();

        // NOW findById issues a fresh SELECT and returns the procedure's value
        assertThat(jpaRepo.findById(id).orElseThrow().getTitle())
                .as("fresh: after em.clear() the DB row is reloaded")
                .isEqualTo("Changed by Procedure");
    }

    // ── Lesson 5: derived queries ─────────────────────────────────────────────
    //
    // Spring Data generates SQL from the method name (findByStock) or from an
    // explicit @Query (findAffordable). The generated implementation runs inside
    // the same transaction, so it sees rows the current transaction has flushed.

    @Test
    void findByStock_returnsOnlyMatchingEntities() {
        jpaRepo.save(new BookEntity("Available Book", null, StockStatus.AVAILABLE, true, new BigDecimal("5.00")));
        jpaRepo.save(new BookEntity("Booked Book", null, StockStatus.BOOKED, true, new BigDecimal("5.00")));
        // Spring Data flushes pending changes before executing a query (FlushModeType.AUTO)

        List<BookEntity> available = jpaRepo.findByStock(StockStatus.AVAILABLE);
        List<BookEntity> booked = jpaRepo.findByStock(StockStatus.BOOKED);

        assertThat(available).isNotEmpty().allMatch(b -> b.getStock() == StockStatus.AVAILABLE);
        assertThat(booked).isNotEmpty().allMatch(b -> b.getStock() == StockStatus.BOOKED);
    }

    @Test
    void findAffordable_returnsBooksBelowOrAtMaxPrice() {
        jpaRepo.save(new BookEntity("Cheap Book", null, StockStatus.AVAILABLE, true, new BigDecimal("5.00")));
        jpaRepo.save(new BookEntity("Expensive Book", null, StockStatus.AVAILABLE, true, new BigDecimal("100.00")));

        List<BookEntity> affordable = jpaRepo.findAffordable(new BigDecimal("10.00"));

        assertThat(affordable).isNotEmpty()
                .allMatch(b -> b.getPrice().compareTo(new BigDecimal("10.00")) <= 0);
        assertThat(affordable)
                .noneMatch(b -> b.getTitle().equals("Expensive Book"));
    }
}
