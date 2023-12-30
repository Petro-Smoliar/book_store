package com.example.book.store.service.impl;

import com.example.book.store.dto.order.OrderDto;
import com.example.book.store.dto.order.OrderItemResponseDto;
import com.example.book.store.dto.order.OrderRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.OrderMapper;
import com.example.book.store.model.CartItem;
import com.example.book.store.model.Order;
import com.example.book.store.model.OrderItem;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.repository.order.OrderRepository;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.service.OrderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto addOrder(OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = getUserShoppingCart();
        Order newOrder = new Order();
        newOrder.setUser(shoppingCart.getUser());
        newOrder.setOrderItems(toSetOrderItem(shoppingCart.getCartItems(), newOrder));
        newOrder.setTotal(getTotal(newOrder.getOrderItems()));
        newOrder.setShippingAddress(orderRequestDto.shippingAddress());
        return orderMapper.toOrderDto(orderRepository.save(newOrder));
    }

    @Override
    public List<Order> getAllOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return orderRepository.findAllByUserEmail(authentication.getName());
    }

    @Override
    public void updateOrderStatus(Long id, Order.Status status) {
        if (orderRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Not found order by id: " + id);
        }
        Order updateOrder = orderRepository.findById(id).get();
        updateOrder.setStatus(status);
        orderRepository.save(updateOrder);
    }

    @Override
    public List<OrderItemResponseDto> getListOrderItem(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Order order =
                orderRepository.findByIdAndUserEmail(orderId, authentication.getName()).orElseThrow(
                    () -> new EntityNotFoundException("Not found order by id: " + orderId));
        if (order.getOrderItems().isEmpty()) {
            return new ArrayList<>();
        }
        return order.getOrderItems().stream()
                   .map(orderMapper::toOrderItemDto)
                   .toList();
    }

    private ShoppingCart getUserShoppingCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return shoppingCartRepository.findByUserEmail(authentication.getName()).orElseThrow(
                    () -> new EntityNotFoundException("Not found shopping cart by email: "
                                                      + authentication.getName())
        );
    }

    private Set<OrderItem> toSetOrderItem(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                   .map(cartItem -> {
                       OrderItem orderItem = orderMapper.toOrderItem(cartItem);
                       orderItem.setOrder(order);
                       return orderItem;
                   })
                   .collect(Collectors.toSet());
    }

    private BigDecimal getTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                   .map(orderItem -> orderItem.getPrice().multiply(
                       BigDecimal.valueOf(orderItem.getQuantity())))
                   .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
