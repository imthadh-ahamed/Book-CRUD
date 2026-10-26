package com.bookCRUD.backend.dto;

import com.bookCRUD.backend.entity.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a new book")
public record CreateBookRequest(
        @Schema(description = "Book title", example = "Clean Code", maxLength = 200)
        @NotBlank @Size(max = 200) String title,

        @Schema(description = "Optional description", example = "A handbook of agile software craftsmanship", maxLength = 2000)
        @Size(max = 2000) String description,

        @Schema(description = "Stock availability status", example = "AVAILABLE")
        @NotNull StockStatus stock,

        @Schema(description = "Whether the book is visible in listings", example = "true")
        @NotNull Boolean isShown,

        @Schema(description = "Price in the store's currency (>= 0.00)", example = "29.99")
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price
) {}
