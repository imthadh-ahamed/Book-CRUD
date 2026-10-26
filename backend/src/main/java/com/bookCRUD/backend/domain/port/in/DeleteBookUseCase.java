package com.bookCRUD.backend.domain.port.in;

/**
 * Inbound port for deleting a book.
 * Throws BookNotFoundException if no book exists with the given id.
 */
public interface DeleteBookUseCase {
    void deleteBook(Long id);
}
