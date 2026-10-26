package com.bookCRUD.backend.application.dto;

import com.bookCRUD.backend.domain.model.StockStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBookRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull StockStatus stock,
        @NotNull Boolean isShown,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price
) {}
