package com.example.book.store.service;

import com.example.book.store.dto.cartitem.CartItemDto;
import com.example.book.store.dto.cartitem.CartItemRequestDto;
import com.example.book.store.dto.cartitem.CartItemRequestUpdateDto;

public interface CartItemService {
    CartItemDto save(CartItemRequestDto cartItemRequestDto);

    CartItemDto update(Long cartItemId, CartItemRequestUpdateDto updateDto);

    void delete(Long cartItemId);
}
