package com.bookCRUD.backend.application.mapper;

import com.bookCRUD.backend.application.dto.BookResponse;
import com.bookCRUD.backend.application.dto.CreateBookRequest;
import com.bookCRUD.backend.application.dto.UpdateBookRequest;
import com.bookCRUD.backend.domain.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(CreateBookRequest req) {
        return new Book(null, req.title(), req.description(), req.stock(), req.isShown(), req.price());
    }

    public Book toDomain(Long id, UpdateBookRequest req) {
        return new Book(id, req.title(), req.description(), req.stock(), req.isShown(), req.price());
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(book.id(), book.title(), book.description(), book.stock(), book.isShown(), book.price());
    }
}
