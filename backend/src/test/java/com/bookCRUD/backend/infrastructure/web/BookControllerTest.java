package com.bookCRUD.backend.infrastructure.web;

import com.bookCRUD.backend.application.mapper.BookMapper;
import com.bookCRUD.backend.domain.exception.BookNotFoundException;
import com.bookCRUD.backend.domain.model.Book;
import com.bookCRUD.backend.domain.model.StockStatus;
import com.bookCRUD.backend.domain.port.in.*;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(BookMapper.class)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean CreateBookUseCase createBook;
    @MockitoBean GetBookUseCase getBook;
    @MockitoBean ListBooksUseCase listBooks;
    @MockitoBean UpdateBookUseCase updateBook;
    @MockitoBean DeleteBookUseCase deleteBook;

    private static final Book SAMPLE = new Book(1L, "Clean Code", "A handbook",
            StockStatus.AVAILABLE, true, new BigDecimal("29.99"));

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
    void createBook_returns201WithBody() throws Exception {
        when(createBook.createBook(any())).thenReturn(SAMPLE);

        mvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void createBook_returns400WhenTitleBlank() throws Exception {
        mvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","stock":"AVAILABLE","isShown":true,"price":10.00}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBook_returns200WhenFound() throws Exception {
        when(getBook.getBook(1L)).thenReturn(SAMPLE);

        mvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBook_returns404WhenNotFound() throws Exception {
        when(getBook.getBook(99L)).thenThrow(new BookNotFoundException(99L));

        mvc.perform(get("/api/v1/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Not Found"));
    }

    @Test
    void listBooks_returns200WithList() throws Exception {
        when(listBooks.listBooks()).thenReturn(List.of(SAMPLE));

        mvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listBooks_returns200WithEmptyList() throws Exception {
        when(listBooks.listBooks()).thenReturn(List.of());

        mvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateBook_returns200WhenFound() throws Exception {
        when(updateBook.updateBook(eq(1L), any())).thenReturn(SAMPLE);

        mvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateBook_returns404WhenNotFound() throws Exception {
        when(updateBook.updateBook(eq(99L), any())).thenThrow(new BookNotFoundException(99L));

        mvc.perform(put("/api/v1/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_returns204WhenFound() throws Exception {
        doNothing().when(deleteBook).deleteBook(1L);

        mvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_returns404WhenNotFound() throws Exception {
        doThrow(new BookNotFoundException(99L)).when(deleteBook).deleteBook(99L);

        mvc.perform(delete("/api/v1/books/99"))
                .andExpect(status().isNotFound());
    }
}
