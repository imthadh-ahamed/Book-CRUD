package com.bookCRUD.backend.entity;

import jakarta.persistence.*;
import com.bookCRUD.backend.entity.StockStatus;

import java.math.BigDecimal;

/**
 * JPA entity mapped to dbo.books.
 *
 * Purpose in this project: satisfying spring.jpa.hibernate.ddl-auto=validate.
 * At startup Hibernate reads the live SQL Server schema and verifies that every
 * column declared here exists with a compatible type. If the schema drifts from
 * the entity mapping the app fails fast with a clear error instead of a silent
 * runtime failure.
 *
 * Actual CRUD goes through BookRepository (JdbcTemplate + stored procedures).
 * This entity is never used as a persistence target in normal request flow.
 */
@Entity
@Table(name = "books", schema = "dbo")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StockStatus stock;

    @Column(name = "is_shown", nullable = false)
    private boolean isShown;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    protected BookEntity() {}

    public BookEntity(String title, String description, StockStatus stock,
                      boolean isShown, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.stock = stock;
        this.isShown = isShown;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public StockStatus getStock() { return stock; }
    public boolean isShown() { return isShown; }
    public BigDecimal getPrice() { return price; }

    // Setter used by BookEntityJpaTest to demonstrate Hibernate dirty checking.
    // Mutating a managed entity's field triggers an automatic UPDATE on flush.
    public void setTitle(String title) { this.title = title; }
}
