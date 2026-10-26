package com.bookCRUD.backend.service;

import com.bookCRUD.backend.dto.BookResponse;
import com.bookCRUD.backend.dto.CreateBookRequest;
import com.bookCRUD.backend.dto.UpdateBookRequest;
import com.bookCRUD.backend.entity.StockStatus;
import com.bookCRUD.backend.exception.BookNotFoundException;
import com.bookCRUD.backend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookService bookService;

    BookResponse sample;
    CreateBookRequest createReq;
    UpdateBookRequest updateReq;

    @BeforeEach
    void setUp() {
        sample = new BookResponse(1L, "Clean Code", "A handbook",
                StockStatus.AVAILABLE, true, new BigDecimal("29.99"));
        createReq = new CreateBookRequest("Clean Code", "A handbook",
                StockStatus.AVAILABLE, true, new BigDecimal("29.99"));
        updateReq = new UpdateBookRequest("Clean Code 2nd", null,
                StockStatus.BOOKED, true, new BigDecimal("34.99"));
    }

    @Test
    void create_delegatesToRepositoryAndReturnsResult() {
        when(bookRepository.create(any(), any(), any(), anyBoolean(), any())).thenReturn(sample);

        BookResponse result = bookService.create(createReq);

        assertThat(result).isEqualTo(sample);
        verify(bookRepository).create("Clean Code", "A handbook",
                StockStatus.AVAILABLE, true, new BigDecimal("29.99"));
    }

    @Test
    void getById_returnsBookWhenFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sample));

        assertThat(bookService.getById(1L)).isEqualTo(sample);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_returnsList() {
        when(bookRepository.findAll()).thenReturn(List.of(sample));

        assertThat(bookService.getAll()).containsExactly(sample);
    }

    @Test
    void getAll_returnsEmptyListWhenNoneExist() {
        when(bookRepository.findAll()).thenReturn(List.of());

        assertThat(bookService.getAll()).isEmpty();
    }

    @Test
    void update_returnsUpdatedBook() {
        BookResponse updated = new BookResponse(1L, "Clean Code 2nd", null,
                StockStatus.BOOKED, true, new BigDecimal("34.99"));
        when(bookRepository.update(eq(1L), any(), any(), any(), anyBoolean(), any()))
                .thenReturn(Optional.of(updated));

        assertThat(bookService.update(1L, updateReq)).isEqualTo(updated);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(bookRepository.update(eq(99L), any(), any(), any(), anyBoolean(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, updateReq))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesWhenExists() {
        when(bookRepository.delete(1L)).thenReturn(true);

        assertThatCode(() -> bookService.delete(1L)).doesNotThrowAnyException();
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(bookRepository.delete(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }
}
