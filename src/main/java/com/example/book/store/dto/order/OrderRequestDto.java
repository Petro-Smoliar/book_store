package com.example.book.store.dto.order;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(@NotNull String shippingAddress) {
}
