package com.example.book.store.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotEmpty
    @Email
    private String username;
    @NotEmpty
    private String password;
}
