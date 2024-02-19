package com.example.book.store.service.impl;

import com.example.book.store.dto.cartitem.CartItemDto;
import com.example.book.store.dto.cartitem.CartItemRequestDto;
import com.example.book.store.dto.cartitem.CartItemRequestUpdateDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.ShoppingCartMapper;
import com.example.book.store.model.Book;
import com.example.book.store.model.CartItem;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.repository.book.BookRepository;
import com.example.book.store.repository.shoppingcart.CartItemRepository;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;

    @Override
    public CartItemDto save(CartItemRequestDto cartItemRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CartItem newCartItem = shoppingCartMapper.toModel(cartItemRequestDto);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(
                authentication.getName()).orElseThrow(() -> new EntityNotFoundException(
                "Not found shopping cart by username: " + authentication.getName()
            )
        );
        Book book = bookRepository.findById(cartItemRequestDto.bookId())
                        .orElseThrow(
                            () -> new EntityNotFoundException("Not found book by id: "
                                                                  + cartItemRequestDto.bookId()));
        newCartItem.setBook(book);
        newCartItem.setShoppingCart(shoppingCart);
        return shoppingCartMapper.toDto(cartItemRepository.save(newCartItem));
    }

    @Override
    public CartItemDto update(Long cartItemId, CartItemRequestUpdateDto updateDto) {
        CartItem cartItem = getCartItemById(cartItemId);
        cartItem.setQuantity(updateDto.quantity());
        return shoppingCartMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long cartItemId) {
        CartItem cartItem = getCartItemById(cartItemId);
        cartItem.setDeleted(true);
        cartItemRepository.save(cartItem);
    }

    private CartItem getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId).orElseThrow(
            () -> new EntityNotFoundException("Not found cart item by id: " + cartItemId)
        );
    }
}
