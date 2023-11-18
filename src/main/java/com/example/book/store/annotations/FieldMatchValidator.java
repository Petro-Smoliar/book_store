package com.example.book.store.annotations;

import com.example.book.store.dto.users.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    @Override
    public void initialize(FieldMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        UserRegistrationRequestDto user = (UserRegistrationRequestDto) obj;
        return user.getPassword().equals(user.getRepeatPassword());
    }
}
