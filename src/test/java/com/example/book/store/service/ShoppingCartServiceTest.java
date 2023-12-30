package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.book.store.dto.shoppingcart.ShoppingCartDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.ShoppingCartMapper;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.service.impl.ShoppingCartServiceImpl;
import java.util.Optional;
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
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("getUserShoppingCart - User has a shopping cart")
    void getUserShoppingCart_UserHasShoppingCart_ShouldReturnShoppingCart() {
        //Given
        String username = "username";
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ShoppingCart shoppingCart = new ShoppingCart().setId(1L);
        when(shoppingCartRepository.findByUserEmail(username))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.getUserShoppingCartDto();
        //Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, Mockito.times(1))
                .findByUserEmail(username);
        verify(shoppingCartMapper, Mockito.times(1))
                .toShoppingCartDto(shoppingCart);
    }

    @Test
    @DisplayName("getUserShoppingCart - User does not have a shopping cart")
    void getUserShoppingCart_UserDoesNotHaveShoppingCart_ShouldThrowEntityNotFoundException() {
        // Given
        String username = "username";
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(shoppingCartRepository.findByUserEmail(username))
                .thenReturn(Optional.empty());

        // When and Then
        assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getUserShoppingCartDto());
        verify(shoppingCartRepository, Mockito.times(1))
                .findByUserEmail(username);
    }

}
