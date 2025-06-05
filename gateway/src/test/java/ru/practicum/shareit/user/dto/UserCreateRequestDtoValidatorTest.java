package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class UserCreateRequestDtoValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_fieldsHasInvalidValues_errorReturned() {
        UserCreateRequestDto createRequest = new UserCreateRequestDto("", "Invalid");
        String nameError = "Name must not be blank";
        String emailFormatError = "Illegal format of email address";

        Set<ConstraintViolation<UserCreateRequestDto>> violations = validator.validate(createRequest);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message").contains(nameError, emailFormatError);

        createRequest = new UserCreateRequestDto("Bruce", "");
        String emailBlankError = "Email must not be blank";

        violations = validator.validate(createRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("message").contains(emailBlankError);
    }

    @Test
    void validate_fieldsHasValidValues_noErrorReturned() {
        UserCreateRequestDto createRequest = new UserCreateRequestDto("Bruce", "pizza@love.it");

        Set<ConstraintViolation<UserCreateRequestDto>> violations = validator.validate(createRequest);

        assertThat(violations).hasSize(0);
    }

}
