package com.example.book.store.service.impl;

import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCart getUserShoppingCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return shoppingCartRepository.findByUserEmail(authentication.getName())
                   .orElseThrow(() -> new EntityNotFoundException(
                       "Not found shopping cart by username: " + authentication.getName()));
    }
}
