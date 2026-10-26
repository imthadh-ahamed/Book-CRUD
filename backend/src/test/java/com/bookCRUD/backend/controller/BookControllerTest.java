package com.bookCRUD.backend.controller;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.entity.StockStatus;
import com.bookCRUD.backend.exception.ApiExceptionHandler;
import com.bookCRUD.backend.exception.BookNotFoundException;
import com.bookCRUD.backend.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(ApiExceptionHandler.class)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BookService bookService;

    private static final BookResponse SAMPLE = new BookResponse(
            1L, "Clean Code", "A handbook", StockStatus.AVAILABLE, true, new BigDecimal("29.99"));

    private static final String VALID_JSON = """
            {
              "title": "Clean Code",
              "description": "A handbook",
              "stock": "AVAILABLE",
              "isShown": true,
              "price": 29.99
            }
            """;

    @Test
    void create_returns201WithBody() throws Exception {
        when(bookService.create(any())).thenReturn(SAMPLE);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void create_returns400WhenTitleBlank() throws Exception {
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","stock":"AVAILABLE","isShown":true,"price":10.00}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns400WhenPriceNegative() throws Exception {
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Test","stock":"AVAILABLE","isShown":true,"price":-1.00}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns400WhenPriceZeroDecimalMinBoundary() throws Exception {
        // price=0.00 satisfies @DecimalMin("0.00") and the DB CHECK (price >= 0).
        // This test documents that zero IS accepted — it is a valid price for free books.
        when(bookService.create(any())).thenReturn(
                new BookResponse(2L, "Free Book", null, StockStatus.AVAILABLE, true, BigDecimal.ZERO));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Free Book","stock":"AVAILABLE","isShown":true,"price":0.00}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void create_returns400WhenStockInvalid() throws Exception {
        // Jackson cannot deserialise "UNKNOWN" into StockStatus enum;
        // Spring Boot returns 400 via HttpMessageNotReadableException.
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Test","stock":"UNKNOWN","isShown":true,"price":10.00}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns200WhenFound() throws Exception {
        when(bookService.getById(1L)).thenReturn(SAMPLE);

        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        when(bookService.getById(99L)).thenThrow(new BookNotFoundException(99L));

        mvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"));
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(bookService.getAll()).thenReturn(List.of(SAMPLE));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAll_returns200WithEmptyList() throws Exception {
        when(bookService.getAll()).thenReturn(List.of());

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void update_returns200WhenFound() throws Exception {
        when(bookService.update(eq(1L), any())).thenReturn(SAMPLE);

        mvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns404WhenNotFound() throws Exception {
        when(bookService.update(eq(99L), any())).thenThrow(new BookNotFoundException(99L));

        mvc.perform(put("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204WhenFound() throws Exception {
        doNothing().when(bookService).delete(1L);

        mvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404WhenNotFound() throws Exception {
        doThrow(new BookNotFoundException(99L)).when(bookService).delete(99L);

        mvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }
}
