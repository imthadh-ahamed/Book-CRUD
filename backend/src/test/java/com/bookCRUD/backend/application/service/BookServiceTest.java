package com.bookCRUD.backend.application.service;

import com.bookCRUD.backend.domain.exception.BookNotFoundException;
import com.bookCRUD.backend.domain.model.Book;
import com.bookCRUD.backend.domain.model.StockStatus;
import com.bookCRUD.backend.domain.port.out.BookPersistencePort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookPersistencePort persistence;

    @InjectMocks
    BookService service;

    Book sample;

    @BeforeEach
    void setUp() {
        sample = new Book(1L, "Clean Code", "A handbook", StockStatus.AVAILABLE, true, new BigDecimal("29.99"));
    }

    @Test
    void createBook_delegatesToPersistenceAndReturnsResult() {
        Book input = new Book(null, "Clean Code", "A handbook", StockStatus.AVAILABLE, true, new BigDecimal("29.99"));
        when(persistence.save(input)).thenReturn(sample);

        Book result = service.createBook(input);

        assertThat(result).isEqualTo(sample);
        verify(persistence).save(input);
    }

    @Test
    void getBook_returnsBookWhenFound() {
        when(persistence.findById(1L)).thenReturn(Optional.of(sample));

        Book result = service.getBook(1L);

        assertThat(result).isEqualTo(sample);
    }

    @Test
    void getBook_throwsWhenNotFound() {
        when(persistence.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBook(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listBooks_returnsAllBooks() {
        when(persistence.findAll()).thenReturn(List.of(sample));

        List<Book> result = service.listBooks();

        assertThat(result).containsExactly(sample);
    }

    @Test
    void listBooks_returnsEmptyListWhenNoneExist() {
        when(persistence.findAll()).thenReturn(List.of());

        assertThat(service.listBooks()).isEmpty();
    }

    @Test
    void updateBook_delegatesToPersistenceWhenExists() {
        when(persistence.existsById(1L)).thenReturn(true);
        when(persistence.update(sample)).thenReturn(sample);

        Book result = service.updateBook(1L, sample);

        assertThat(result).isEqualTo(sample);
        verify(persistence).update(sample);
    }

    @Test
    void updateBook_throwsWhenNotFound() {
        when(persistence.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.updateBook(99L, sample))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteBook_throwsWhenNotFound() {
        when(persistence.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteBook(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");

        verify(persistence, never()).deleteById(anyLong());
    }
}
