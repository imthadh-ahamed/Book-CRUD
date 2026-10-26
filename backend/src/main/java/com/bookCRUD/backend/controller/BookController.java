package com.bookCRUD.backend.controller;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.dto.CreateBookRequest;
import com.bookCRUD.backend.dto.UpdateBookRequest;
import com.bookCRUD.backend.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created",
            content = @Content(schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body (validation failure)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book found",
            content = @Content(schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "404", description = "No book with the given id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookResponse getById(
            @Parameter(description = "ID of the book to retrieve", example = "1")
            @PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    @Operation(summary = "List all books")
    @ApiResponse(responseCode = "200", description = "List of all books (may be empty)")
    public List<BookResponse> getAll() {
        return bookService.getAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book updated",
            content = @Content(schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body (validation failure)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "No book with the given id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookResponse update(
            @Parameter(description = "ID of the book to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book deleted"),
        @ApiResponse(responseCode = "404", description = "No book with the given id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void delete(
            @Parameter(description = "ID of the book to delete", example = "1")
            @PathVariable Long id) {
        bookService.delete(id);
    }
}
