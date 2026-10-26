package com.bookCRUD.backend.domain.port.out;

import com.bookCRUD.backend.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookPersistencePort {
    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book update(Book book);
    void deleteById(Long id);
    boolean existsById(Long id);
}
