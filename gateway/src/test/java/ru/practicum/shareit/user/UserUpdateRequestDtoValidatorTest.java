package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

public class UserUpdateRequestDtoValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_fieldsHasInvalidValues_errorReturned() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("", "Invalid");
        String emailFormatError = "Illegal format of email address";

        Set<ConstraintViolation<UserUpdateRequestDto>> violations = validator.validate(updateRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("message").contains(emailFormatError);
    }

    @Test
    void validate_fieldsHasValidValues_noErrorReturned() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("", "pizza@love.it");

        Set<ConstraintViolation<UserUpdateRequestDto>> violations = validator.validate(updateRequest);

        assertThat(violations).hasSize(0);
    }

}
