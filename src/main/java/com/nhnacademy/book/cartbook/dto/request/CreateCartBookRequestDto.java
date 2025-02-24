package com.nhnacademy.book.cartbook.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record CreateCartBookRequestDto(
        @Min(1) Long sellingBookId,
        @Min(1) int quantity
) {}
