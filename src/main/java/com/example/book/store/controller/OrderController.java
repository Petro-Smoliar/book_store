package com.example.book.store.controller;

import com.example.book.store.dto.order.OrderDto;
import com.example.book.store.dto.order.OrderItemResponseDto;
import com.example.book.store.dto.order.OrderRequestDto;
import com.example.book.store.model.Order;
import com.example.book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "order management", description = "Endpoints for managing order")
@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Place an order",
            description = "Allows a user to place an order by sending a "
                              + "POST request to /api/orders."
    )
    @PostMapping("/orders")
    public OrderDto addOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        return orderService.addOrder(orderRequestDto);
    }

    @Operation(
            summary = "Get all orders",
            description = "Allows a user to retrieve their order history by sending a "
                          + "GET request to /api/orders."
    )
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(
            summary = "Update order status",
            description = "Allows an admin to update the status of an order by sending a "
                          + "PATCH request to /api/orders/{id}."
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/orders/{id}")
    public void updateOrderStatus(@PathVariable Long id, @RequestBody @Valid Order.Status status) {
        orderService.updateOrderStatus(id, status);
    }

    @Operation(
            summary = "Get order items",
            description = "Allows a user to retrieve all OrderItems for a specific order by "
                              + "sending a GET request to /api/orders/{orderId}/items."
    )
    @GetMapping("api/orders/{orderId}/items")
    public List<OrderItemResponseDto> getListOrderItem(@PathVariable Long orderId) {
        return orderService.getListOrderItem(orderId);
    }
}
