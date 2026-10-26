package com.bookCRUD.backend.domain.port.in;

import com.bookCRUD.backend.domain.model.Book;

/**
 * Inbound port for creating a book.
 *
 * The controller depends on this interface, not on BookService directly.
 * This demonstrates the Dependency Inversion Principle: the web layer
 * depends on an abstraction, not on a concrete implementation.
 *
 * The input Book has id=null (not yet persisted).
 * The returned Book has the database-assigned id.
 */
public interface CreateBookUseCase {
    Book createBook(Book book);
}
