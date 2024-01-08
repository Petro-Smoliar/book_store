package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;
import com.example.book.store.exception.RegistrationException;
import com.example.book.store.mapper.UserMapper;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.model.User;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.repository.user.UserRepository;
import com.example.book.store.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("save - Successfully save user")
    void save_SuccessfullySaveUser_ShouldReturnUserDto() {
        // Given
        User newUser = new User();
        newUser.setId(1L);
        newUser.setPassword("password");
        newUser.setEmail("test@example.com");
        UserDto expected = new UserDto();
        expected.setEmail("test@example.com");
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();

        when(userMapper.toModel(requestDto)).thenReturn(newUser);
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(shoppingCartRepository.save(any())).thenReturn(new ShoppingCart());
        when(userMapper.toDto(newUser)).thenReturn(expected);
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));

        // When
        UserDto actual = userService.save(requestDto);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);

        // Verify method invocations
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode("password");
        verify(shoppingCartRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(newUser);
        verify(userRepository, times(1)).findByEmail(newUser.getEmail());
    }

    @Test
    @DisplayName("save - RegistrationException thrown during save")
    void save_RegistrationExceptionThrownDuringSave_ShouldThrowRegistrationException() {
        // Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setPassword("password");

        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");

        when(userMapper.toModel(requestDto)).thenReturn(newUser);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(shoppingCartRepository.save(any())).thenThrow(new RuntimeException("Some error"));

        // When and Then
        assertThrows(RegistrationException.class, () -> userService.save(requestDto));

        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode("password");
        verify(shoppingCartRepository, times(1)).save(any());
        verify(userRepository, times(0)).findByEmail(anyString());
    }
}
