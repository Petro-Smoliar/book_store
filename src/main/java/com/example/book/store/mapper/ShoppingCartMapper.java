package com.example.book.store.mapper;

import com.example.book.store.config.MapperConfig;
import com.example.book.store.dto.cartitem.CartItemDto;
import com.example.book.store.dto.cartitem.CartItemRequestDto;
import com.example.book.store.dto.shoppingcart.ShoppingCartDto;
import com.example.book.store.model.Book;
import com.example.book.store.model.CartItem;
import com.example.book.store.model.ShoppingCart;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "shoppingCart.user.id")
    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);

    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toModel(CartItemRequestDto cartItemRequestDto);

    @Mapping(target = "bookId", source = "cartItem.book.id")
    @Mapping(target = "bookTitle", source = "cartItem.book.title")
    CartItemDto toDto(CartItem cartItem);

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }

    @AfterMapping
    default void setCartItemDto(@MappingTarget ShoppingCartDto shoppingCartDto,
                                ShoppingCart shoppingCart) {
        shoppingCartDto.setCartItems(shoppingCart.getCartItems().stream()
                                         .map(this::toDto)
                                         .collect(Collectors.toSet()));
    }
}
