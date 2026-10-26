package com.bookCRUD.backend.domain.port.in;

import com.bookCRUD.backend.domain.model.Book;

import java.util.List;

/**
 * Inbound port for listing all books.
 * Returns an empty list when no books exist; never returns null.
 */
public interface ListBooksUseCase {
    List<Book> listBooks();
}
