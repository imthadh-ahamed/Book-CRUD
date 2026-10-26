package com.bookCRUD.backend.dto;

import com.bookCRUD.backend.entity.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "A book returned by the API")
public record BookResponse(
        @Schema(description = "Auto-generated database ID", example = "1")
        Long id,

        @Schema(description = "Book title", example = "Clean Code")
        String title,

        @Schema(description = "Optional description", example = "A handbook of agile software craftsmanship")
        String description,

        @Schema(description = "Stock availability status", example = "AVAILABLE")
        StockStatus stock,

        @Schema(description = "Whether the book is visible in listings", example = "true")
        boolean isShown,

        @Schema(description = "Price in the store's currency", example = "29.99")
        BigDecimal price
) {}
