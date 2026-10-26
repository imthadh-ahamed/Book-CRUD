package com.bookCRUD.backend.domain.port.in;

import com.bookCRUD.backend.domain.model.Book;

/**
 * Inbound port for replacing all fields of an existing book (full update / PUT semantics).
 * Throws BookNotFoundException if no book exists with the given id.
 *
 * The id in the Book argument is the path-parameter id, not a client-supplied value.
 * The mapper sets it before calling this use case.
 */
public interface UpdateBookUseCase {
    Book updateBook(Long id, Book book);
}
