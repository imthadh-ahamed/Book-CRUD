package com.bookCRUD.backend.dto;

import com.bookCRUD.backend.entity.StockStatus;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        String description,
        StockStatus stock,
        boolean isShown,
        BigDecimal price
) {}
