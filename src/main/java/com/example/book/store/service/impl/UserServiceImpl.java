package com.example.book.store.service.impl;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;
import com.example.book.store.exception.RegistrationException;
import com.example.book.store.mapper.UserMapper;
import com.example.book.store.model.User;
import com.example.book.store.repository.user.UserRepository;
import com.example.book.store.service.UserService;
import com.example.book.store.validation.FieldMatchValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FieldMatchValidator fieldMatchValidator;

    @Override
    public UserDto save(@Valid UserRegistrationRequestDto requestDto) {
        try {
            User newUser = userRepository.save(userMapper.toModel(requestDto));
            return userMapper.toDto(newUser);
        } catch (Exception e) {
            throw new RegistrationException("Error while registering user", e);
        }
    }
}