package com.bookCRUD.backend.controller;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.dto.CreateBookRequest;
import com.bookCRUD.backend.dto.UpdateBookRequest;
import com.bookCRUD.backend.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book CRUD operations")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book")
    public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id")
    public BookResponse getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    @Operation(summary = "List all books")
    public List<BookResponse> getAll() {
        return bookService.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
