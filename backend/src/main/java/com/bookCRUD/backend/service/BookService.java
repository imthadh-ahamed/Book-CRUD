package com.bookCRUD.backend.service;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.dto.CreateBookRequest;
import com.bookCRUD.backend.dto.UpdateBookRequest;
import com.bookCRUD.backend.exception.BookNotFoundException;
import com.bookCRUD.backend.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse create(CreateBookRequest req) {
        return bookRepository.create(
                req.title(), req.description(), req.stock(), req.isShown(), req.price());
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAll() {
        return bookRepository.findAll();
    }

    public BookResponse update(Long id, UpdateBookRequest req) {
        return bookRepository.update(id, req.title(), req.description(),
                        req.stock(), req.isShown(), req.price())
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public void delete(Long id) {
        boolean deleted = bookRepository.delete(id);
        if (!deleted) {
            throw new BookNotFoundException(id);
        }
    }
}
