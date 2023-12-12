package com.example.book.store.dto.order;

import com.example.book.store.model.Order;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record OrderRequestStatusDto(
        @Enumerated(EnumType.STRING)
        Order.Status status) {
}
