package com.bookCRUD.backend.domain.model;

/**
 * Represents the availability state of a book in the catalogue.
 *
 * Stored as a string in SQL Server (NVARCHAR column with a CHECK constraint).
 * Never add ordinal-based serialization — the enum name is the contract.
 */
public enum StockStatus {
    AVAILABLE,
    BOOKED,
    OUT_OF_STOCK
}
