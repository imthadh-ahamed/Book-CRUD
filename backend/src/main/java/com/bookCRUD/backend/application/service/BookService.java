package com.bookCRUD.backend.application.service;

import com.bookCRUD.backend.domain.exception.BookNotFoundException;
import com.bookCRUD.backend.domain.model.Book;
import com.bookCRUD.backend.domain.port.in.*;
import com.bookCRUD.backend.domain.port.out.BookPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService implements
        CreateBookUseCase,
        GetBookUseCase,
        ListBooksUseCase,
        UpdateBookUseCase,
        DeleteBookUseCase {

    private final BookPersistencePort persistence;

    public BookService(BookPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public Book createBook(Book book) {
        return persistence.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(Long id) {
        return persistence.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> listBooks() {
        return persistence.findAll();
    }

    @Override
    public Book updateBook(Long id, Book book) {
        if (!persistence.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        return persistence.update(book);
    }

    @Override
    public void deleteBook(Long id) {
        if (!persistence.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        persistence.deleteById(id);
    }
}
