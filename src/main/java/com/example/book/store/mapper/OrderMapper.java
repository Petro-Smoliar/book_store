package com.example.book.store.mapper;

import com.example.book.store.config.MapperConfig;
import com.example.book.store.dto.order.OrderDto;
import com.example.book.store.dto.order.OrderItemResponseDto;
import com.example.book.store.model.CartItem;
import com.example.book.store.model.Order;
import com.example.book.store.model.OrderItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "bookId", source = "orderItem.book.id")
    OrderItemResponseDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "price", source = "cartItem.book.price")
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItem(CartItem cartItem);

    @Mapping(target = "userId", source = "order.user.id")
    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "dtoSetFromSetModel")
    OrderDto toOrderDto(Order order);

    @Named("dtoSetFromSetModel")
    default Set<OrderItemResponseDto> dtoSetFromSetModel(Set<OrderItem> orderItems) {
        return orderItems.stream()
                   .map(this::toOrderItemDto)
                   .collect(Collectors.toSet());

    }
}
