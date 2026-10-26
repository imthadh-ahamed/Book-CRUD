package com.bookCRUD.backend.repository;

import com.bookCRUD.backend.entity.StockStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class BookRepositoryTest {

    @Mock
    JdbcTemplate jdbc;

    @InjectMocks
    BookRepository bookRepository;

    @Test
    void create_throwsIllegalStateExceptionWhenProcedureReturnsNoRows() {
        // Simulate Book_Create returning an empty result set (e.g. broken SCOPE_IDENTITY).
        // doAnswer avoids Mockito varargs generic-type matching issues.
        doAnswer(inv -> List.of())
                .when(jdbc).query(anyString(), any(RowMapper.class), any(), any(), any(), any(), any());

        assertThatThrownBy(() ->
                bookRepository.create("Title", null, StockStatus.AVAILABLE, true, new BigDecimal("9.99"))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Book_Create returned no rows");
    }
}
