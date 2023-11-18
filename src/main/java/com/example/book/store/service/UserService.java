package com.example.book.store.service;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;

public interface UserService {
    UserDto save(UserRegistrationRequestDto requestDto);
}
