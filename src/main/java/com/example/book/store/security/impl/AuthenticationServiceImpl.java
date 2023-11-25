package com.example.book.store.security.impl;

import com.example.book.store.dto.users.UserLoginRequestDto;
import com.example.book.store.dto.users.UserLoginResponseDto;
import com.example.book.store.security.AuthenticationService;
import com.example.book.store.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserLoginResponseDto authentication(UserLoginRequestDto loginRequestDto) {
        final Authentication authentication =
                authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getUsername(),
                    loginRequestDto.getPassword()
                )
            );
        return new UserLoginResponseDto(
            jwtUtil.generateToken(authentication.getName())
        );
    }
}
