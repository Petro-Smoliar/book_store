package com.example.book.store.controller;

import com.example.book.store.dto.cartitem.CartItemDto;
import com.example.book.store.dto.cartitem.CartItemRequestDto;
import com.example.book.store.dto.cartitem.CartItemRequestUpdateDto;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.service.CartItemService;
import com.example.book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
public class ShoppingCartController {
    private final CartItemService cartItemService;
    private final ShoppingCartService shoppingCartService;

    @Operation(
            summary = "Get User's Shopping Cart",
            description = "Retrieve the shopping cart associated with the authenticated user."
    )
    @GetMapping("/cart")
    public ShoppingCart getUserShoppingCart() {
        return shoppingCartService.getUserShoppingCart();
    }

    @Operation(
            summary = "Add Item to Shopping Cart",
            description = "Add a new item to the user's shopping cart based on the provided "
                              + "information."
    )
    @PostMapping("/cart")
    public CartItemDto addCartItem(@RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        return cartItemService.save(cartItemRequestDto);
    }

    @Operation(
            summary = "Update Shopping Cart Item",
            description = "Update the quantity or other details of a specific item in the user's "
                              + "shopping cart."
    )
    @PutMapping("/cart/cart-items/{cartItemId}")
    public CartItemDto updateCartItem(@PathVariable Long cartItemId,
                                      @RequestBody CartItemRequestUpdateDto updateDto) {
        return cartItemService.update(cartItemId, updateDto);
    }

    @Operation(
            summary = "Delete Shopping Cart Item",
            description = "Remove a specific item from the user's shopping cart."
    )
    @DeleteMapping("/cart/cart-items/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.delete(cartItemId);
    }
}
