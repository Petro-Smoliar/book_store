package com.example.book.store.service;

import com.example.book.store.dto.order.OrderDto;
import com.example.book.store.dto.order.OrderItemResponseDto;
import com.example.book.store.dto.order.OrderRequestDto;
import com.example.book.store.model.Order;
import java.util.List;

public interface OrderService {
    OrderDto addOrder(OrderRequestDto orderRequestDto);

    List<OrderDto> getAllOrders();

    void updateOrderStatus(Long id, Order.Status status);

    List<OrderItemResponseDto> getListOrderItem(Long orderId);

}
