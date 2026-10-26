package com.bookCRUD.backend.domain.exception;

/**
 * Thrown when a requested book does not exist.
 * The REST layer maps this to HTTP 404.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}
