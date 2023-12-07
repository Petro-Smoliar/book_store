package com.example.book.store.controller;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserLoginRequestDto;
import com.example.book.store.dto.users.UserLoginResponseDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;
import com.example.book.store.security.AuthenticationService;
import com.example.book.store.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/registration")
    public UserDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.save(requestDto);
    }

    @PostMapping("/auth/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authentication(request);
    }
}
