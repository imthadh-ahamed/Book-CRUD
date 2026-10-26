package com.bookCRUD.backend.domain.port.in;

import com.bookCRUD.backend.domain.model.Book;

/**
 * Inbound port for retrieving a single book by its id.
 * Throws BookNotFoundException if no book exists with that id.
 */
public interface GetBookUseCase {
    Book getBook(Long id);
}
