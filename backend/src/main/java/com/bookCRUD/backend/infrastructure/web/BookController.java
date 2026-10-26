package com.bookCRUD.backend.infrastructure.web;

import com.bookCRUD.backend.application.dto.BookResponse;
import com.bookCRUD.backend.application.dto.CreateBookRequest;
import com.bookCRUD.backend.application.dto.UpdateBookRequest;
import com.bookCRUD.backend.application.mapper.BookMapper;
import com.bookCRUD.backend.domain.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "Book CRUD operations")
public class BookController {

    private final CreateBookUseCase createBook;
    private final GetBookUseCase getBook;
    private final ListBooksUseCase listBooks;
    private final UpdateBookUseCase updateBook;
    private final DeleteBookUseCase deleteBook;
    private final BookMapper mapper;

    public BookController(
            CreateBookUseCase createBook,
            GetBookUseCase getBook,
            ListBooksUseCase listBooks,
            UpdateBookUseCase updateBook,
            DeleteBookUseCase deleteBook,
            BookMapper mapper) {
        this.createBook = createBook;
        this.getBook = getBook;
        this.listBooks = listBooks;
        this.updateBook = updateBook;
        this.deleteBook = deleteBook;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book")
    public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
        return mapper.toResponse(createBook.createBook(mapper.toDomain(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id")
    public BookResponse getById(@PathVariable Long id) {
        return mapper.toResponse(getBook.getBook(id));
    }

    @GetMapping
    @Operation(summary = "List all books")
    public List<BookResponse> list() {
        return listBooks.listBooks().stream().map(mapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request) {
        return mapper.toResponse(updateBook.updateBook(id, mapper.toDomain(id, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book")
    public void delete(@PathVariable Long id) {
        deleteBook.deleteBook(id);
    }
}
