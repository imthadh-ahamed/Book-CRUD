package com.bookCRUD.backend.application.dto;

import com.bookCRUD.backend.domain.model.StockStatus;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        String description,
        StockStatus stock,
        boolean isShown,
        BigDecimal price
) {}
