package com.bookCRUD.backend.repository;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.entity.StockStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for BookRepository against a real SQL Server instance.
 *
 * Testcontainers starts an isolated SQL Server Docker container for this test
 * class. Flyway applies all migrations (V1–V3) to that container at startup.
 * The developer's BookCrudDev database is never touched.
 *
 * Requires Docker to be running. If Docker is unavailable, these tests will
 * fail at container startup with a clear error — they will not silently pass.
 */
@Tag("integration")
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// Override the test-scoped application.properties that disables DataSource/JPA/Flyway.
// This integration test needs a real DataSource (provided by the container) and Flyway
// to apply migrations before the repository is exercised.
@TestPropertySource(properties = "spring.autoconfigure.exclude=")
class BookRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    // acceptLicense() is required by the SQL Server image EULA.
    // The password meets SQL Server complexity requirements (upper + lower + digit + symbol).
    static MSSQLServerContainer<?> sqlServer =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                    .acceptLicense();

    @Autowired
    BookRepository bookRepository;

    // ── helpers ────────────────────────────────────────────────────────────────

    private BookResponse createSample(String title, String description,
                                      StockStatus stock, boolean isShown, String price) {
        return bookRepository.create(title, description, stock, isShown, new BigDecimal(price));
    }

    private void safeDelete(Long id) {
        bookRepository.delete(id);
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void create_persistsBookAndReturnsGeneratedId() {
        BookResponse created = createSample("Clean Code", "A handbook",
                StockStatus.AVAILABLE, true, "29.99");
        try {
            assertThat(created.id()).isNotNull().isPositive();
            assertThat(created.title()).isEqualTo("Clean Code");
            assertThat(created.description()).isEqualTo("A handbook");
            assertThat(created.stock()).isEqualTo(StockStatus.AVAILABLE);
            assertThat(created.isShown()).isTrue();
            assertThat(created.price()).isEqualByComparingTo(new BigDecimal("29.99"));
        } finally {
            safeDelete(created.id());
        }
    }

    @Test
    void create_acceptsNullDescription() {
        BookResponse created = createSample("No Desc Book", null,
                StockStatus.BOOKED, false, "5.00");
        try {
            assertThat(created.description()).isNull();
        } finally {
            safeDelete(created.id());
        }
    }

    @Test
    void create_generatesDistinctIdsForEachInsert() {
        BookResponse first = createSample("Book A", null, StockStatus.AVAILABLE, true, "1.00");
        BookResponse second = createSample("Book B", null, StockStatus.AVAILABLE, true, "2.00");
        try {
            assertThat(first.id()).isNotEqualTo(second.id());
        } finally {
            safeDelete(first.id());
            safeDelete(second.id());
        }
    }

    // ── get by id ──────────────────────────────────────────────────────────────

    @Test
    void findById_returnsBookWhenExists() {
        BookResponse created = createSample("Get Me", null, StockStatus.OUT_OF_STOCK, true, "0.00");
        try {
            Optional<BookResponse> found = bookRepository.findById(created.id());
            assertThat(found).isPresent();
            assertThat(found.get().title()).isEqualTo("Get Me");
            assertThat(found.get().stock()).isEqualTo(StockStatus.OUT_OF_STOCK);
            assertThat(found.get().price()).isEqualByComparingTo(BigDecimal.ZERO);
        } finally {
            safeDelete(created.id());
        }
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        Optional<BookResponse> result = bookRepository.findById(Long.MAX_VALUE);
        assertThat(result).isEmpty();
    }

    // ── list ───────────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllPersistedBooks() {
        BookResponse b1 = createSample("List Book 1", null, StockStatus.AVAILABLE, true, "10.00");
        BookResponse b2 = createSample("List Book 2", null, StockStatus.BOOKED, true, "20.00");
        try {
            List<BookResponse> all = bookRepository.findAll();
            assertThat(all).extracting(BookResponse::id)
                    .contains(b1.id(), b2.id());
        } finally {
            safeDelete(b1.id());
            safeDelete(b2.id());
        }
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void update_modifiesAllFieldsAndReturnsUpdatedBook() {
        BookResponse created = createSample("Old Title", "Old desc",
                StockStatus.AVAILABLE, true, "10.00");
        try {
            Optional<BookResponse> updated = bookRepository.update(
                    created.id(), "New Title", null, StockStatus.BOOKED, false, new BigDecimal("99.99"));
            assertThat(updated).isPresent();
            assertThat(updated.get().title()).isEqualTo("New Title");
            assertThat(updated.get().description()).isNull();
            assertThat(updated.get().stock()).isEqualTo(StockStatus.BOOKED);
            assertThat(updated.get().isShown()).isFalse();
            assertThat(updated.get().price()).isEqualByComparingTo(new BigDecimal("99.99"));
        } finally {
            safeDelete(created.id());
        }
    }

    @Test
    void update_returnsEmptyWhenBookDoesNotExist() {
        Optional<BookResponse> result = bookRepository.update(
                Long.MAX_VALUE, "X", null, StockStatus.AVAILABLE, true, new BigDecimal("1.00"));
        assertThat(result).isEmpty();
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_returnsTrueAndRemovesBook() {
        BookResponse created = createSample("Delete Me", null, StockStatus.AVAILABLE, true, "1.00");

        boolean deleted = bookRepository.delete(created.id());

        assertThat(deleted).isTrue();
        assertThat(bookRepository.findById(created.id())).isEmpty();
    }

    @Test
    void delete_returnsFalseWhenBookDoesNotExist() {
        boolean result = bookRepository.delete(Long.MAX_VALUE);
        assertThat(result).isFalse();
    }

    // ── decimal precision and enum values ──────────────────────────────────────

    @Test
    void create_preservesDecimalPrecisionAndScale() {
        BookResponse created = createSample("Precise Price", null,
                StockStatus.AVAILABLE, true, "123456789.99");
        try {
            assertThat(created.price()).isEqualByComparingTo(new BigDecimal("123456789.99"));
        } finally {
            safeDelete(created.id());
        }
    }

    @Test
    void create_roundTripsAllStockStatusValues() {
        for (StockStatus status : StockStatus.values()) {
            BookResponse created = createSample("Status Test", null, status, true, "1.00");
            try {
                Optional<BookResponse> found = bookRepository.findById(created.id());
                assertThat(found).isPresent();
                assertThat(found.get().stock()).isEqualTo(status);
            } finally {
                safeDelete(created.id());
            }
        }
    }
}
