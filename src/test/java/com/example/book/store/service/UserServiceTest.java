package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;
import com.example.book.store.exception.RegistrationException;
import com.example.book.store.mapper.UserMapper;
import com.example.book.store.model.User;
import com.example.book.store.repository.user.UserRepository;
import com.example.book.store.service.impl.UserServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("save - Successfully save user")
    void save_SuccessfullySaveUser_ShouldReturnUserDto() {
        // Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        User newUser = new User();
        newUser.setPassword("password");
        UserDto expected = new UserDto();
        when(userMapper.toModel(requestDto)).thenReturn(newUser);
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userMapper.toDto(newUser)).thenReturn(expected);
        // When
        UserDto actual = userService.save(requestDto);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(newUser);
        verify(userMapper, times(1)).toDto(newUser);
    }

    @Test
    @DisplayName("save - RegistrationException thrown during save")
    void save_RegistrationExceptionThrownDuringSave_ShouldThrowRegistrationException() {
        // Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setPassword("password");
        when(userMapper.toModel(requestDto)).thenReturn(new User());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Some error"));
        // When and Then
        assertThrows(RegistrationException.class, () -> userService.save(requestDto));
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(0)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
