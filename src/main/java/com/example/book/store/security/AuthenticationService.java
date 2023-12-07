package com.example.book.store.security;

import com.example.book.store.dto.users.UserLoginRequestDto;
import com.example.book.store.dto.users.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authentication(UserLoginRequestDto loginRequestDto);
}
