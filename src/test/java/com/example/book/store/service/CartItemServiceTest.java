package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.example.book.store.service.impl.CartItemServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Test
    @DisplayName("save - Successfully save cart item")
    void save_SuccessfullySaveCartItem_ShouldReturnCartItemDto() {
        // Given
        CartItem newCartItem = new CartItem();
        newCartItem.setId(1L);
        newCartItem.setQuantity(12);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(1L, 12L);
        when(shoppingCartMapper.toModel(cartItemRequestDto)).thenReturn(newCartItem);
        when(bookRepository.findById(cartItemRequestDto.bookId()))
                .thenReturn(Optional.of(new Book().setId(1L)));
        ShoppingCart shoppingCart = new ShoppingCart();
        when(shoppingCartRepository.findByUserEmail("testUser"))
                .thenReturn(Optional.of(shoppingCart));
        CartItemDto expected = new CartItemDto();
        expected.setId(1L);
        expected.setQuantity(12L);
        when(cartItemRepository.save(newCartItem)).thenReturn(newCartItem);
        when(shoppingCartMapper.toDto(newCartItem)).thenReturn(expected);
        //When
        CartItemDto actual = cartItemService.save(cartItemRequestDto);
        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(shoppingCartMapper, Mockito.times(1))
                .toModel(cartItemRequestDto);
        verify(shoppingCartRepository, Mockito.times(1))
                .findByUserEmail("testUser");
        verify(cartItemRepository, Mockito.times(1)).save(newCartItem);
        verify(shoppingCartMapper, Mockito.times(1)).toDto(newCartItem);
    }

    @Test
    @DisplayName("update - Successfully update cart item")
    void update_SuccessfullyUpdateCartItem_ShouldReturnCartItemDto() {
        // Given
        Long cartItemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        CartItemDto expected = new CartItemDto();
        expected.setQuantity(10L);
        CartItemRequestUpdateDto updateDto = new CartItemRequestUpdateDto(10);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(cartItem)).thenReturn(expected);
        // When
        CartItemDto actual = cartItemService.update(cartItemId, updateDto);
        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(cartItemRepository, Mockito.times(1)).findById(cartItemId);
        verify(cartItemRepository, Mockito.times(1)).save(cartItem);
        verify(shoppingCartMapper, Mockito.times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("delete - Successfully delete cart item")
    void delete_SuccessfullyDeleteCartItem_ShouldNotThrowException() {
        // Given
        Long cartItemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        // When and Then
        assertDoesNotThrow(() -> cartItemService.delete(cartItemId));
        verify(cartItemRepository, Mockito.times(1)).findById(cartItemId);
        verify(cartItemRepository, Mockito.times(1)).save(cartItem);
    }

    @Test
    @DisplayName("delete - Cart item not found")
    void delete_CartItemNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        Long cartItemId = 1L;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        // When and Then
        assertThrows(EntityNotFoundException.class, () -> cartItemService.delete(cartItemId));
        verify(cartItemRepository, Mockito.times(1)).findById(cartItemId);
        verify(cartItemRepository, Mockito.never()).save(any());
    }
}
