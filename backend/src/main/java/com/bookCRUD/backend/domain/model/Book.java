package com.bookCRUD.backend.domain.model;

import java.math.BigDecimal;

/**
 * Core domain object representing a book in the catalogue.
 *
 * This is a plain Java record with no Spring, JPA, or HTTP annotations.
 * The domain layer depends on nothing outside itself — no frameworks, no SQL, no web.
 *
 * id is null for books that have not yet been persisted (creation path).
 * The persistence adapter fills in the database-assigned id and returns the saved Book.
 *
 * BigDecimal is used for price because floating-point types (double/float) cannot
 * represent decimal values exactly, which leads to rounding errors in financial data.
 */
public record Book(
        Long id,
        String title,
        String description,
        StockStatus stock,
        boolean isShown,
        BigDecimal price
) {}
