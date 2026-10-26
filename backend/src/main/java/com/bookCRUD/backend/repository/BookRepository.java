package com.bookCRUD.backend.repository;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.entity.StockStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Calls SQL Server stored procedures via JdbcTemplate.
 *
 * Why JdbcTemplate instead of JPA stored-procedure APIs?
 * JPA's @NamedStoredProcedureQuery maps output parameters but cannot cleanly
 * map a procedure that returns a SELECT result set as a managed entity without
 * a matching query. JdbcTemplate.query() / queryForObject() let us map the
 * result set columns explicitly with a RowMapper, which matches exactly what
 * each procedure SELECTs (id, title, description, stock, is_shown, price).
 */
@Repository
public class BookRepository {

    private final JdbcTemplate jdbc;

    public BookRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<BookResponse> BOOK_ROW_MAPPER = new RowMapper<>() {
        @Override
        public BookResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookResponse(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    StockStatus.valueOf(rs.getString("stock")),
                    rs.getBoolean("is_shown"),
                    rs.getBigDecimal("price")
            );
        }
    };

    public BookResponse create(String title, String description, StockStatus stock,
                               boolean isShown, java.math.BigDecimal price) {
        List<BookResponse> result = jdbc.query(
                "EXEC dbo.Book_Create @title=?, @description=?, @stock=?, @is_shown=?, @price=?",
                BOOK_ROW_MAPPER,
                title, description, stock.name(), isShown, price
        );
        if (result.isEmpty()) {
            throw new IllegalStateException(
                    "Book_Create returned no rows — the INSERT may have failed silently. " +
                    "Check SCOPE_IDENTITY() and the stored procedure definition.");
        }
        return result.get(0);
    }

    public Optional<BookResponse> findById(Long id) {
        try {
            BookResponse book = jdbc.queryForObject(
                    "EXEC dbo.Book_GetById @id=?",
                    BOOK_ROW_MAPPER,
                    id
            );
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<BookResponse> findAll() {
        return jdbc.query("EXEC dbo.Book_GetAll", BOOK_ROW_MAPPER);
    }

    public Optional<BookResponse> update(Long id, String title, String description,
                                         StockStatus stock, boolean isShown,
                                         java.math.BigDecimal price) {
        // Book_Update returns 0 rows when the id does not exist.
        List<BookResponse> result = jdbc.query(
                "EXEC dbo.Book_Update @id=?, @title=?, @description=?, @stock=?, @is_shown=?, @price=?",
                BOOK_ROW_MAPPER,
                id, title, description, stock.name(), isShown, price
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean delete(Long id) {
        // Book_Delete returns rows_affected; 0 means the id did not exist.
        Integer affected = jdbc.queryForObject(
                "EXEC dbo.Book_Delete @id=?",
                (rs, n) -> rs.getInt("rows_affected"),
                id
        );
        return affected != null && affected > 0;
    }
}
