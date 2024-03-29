package com.example.book.store.service.impl;

import com.example.book.store.dto.users.UserDto;
import com.example.book.store.dto.users.UserRegistrationRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.exception.RegistrationException;
import com.example.book.store.mapper.UserMapper;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.model.User;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.repository.user.UserRepository;
import com.example.book.store.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public UserDto save(@Valid UserRegistrationRequestDto requestDto) {
        try {
            User newUser = userMapper.toModel(requestDto);
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(newUser);
            shoppingCartRepository.save(shoppingCart);
            return userMapper.toDto(
                userRepository.findByEmail(
                    newUser.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Not found user by email: "
                                                                       + newUser.getEmail()))
            );
        } catch (Exception e) {
            throw new RegistrationException("Error while registering user", e);
        }
    }
}
