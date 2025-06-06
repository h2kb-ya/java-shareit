package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;

public class CommentCreateRequestDtoValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_fieldsHasInvalidValues_errorReturned() {
        CommentCreateRequestDto createRequestDto = new CommentCreateRequestDto("");
        String textError = "Comment text must not be blank";
        Set<ConstraintViolation<CommentCreateRequestDto>> violations = validator.validate(createRequestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("message").contains(textError);
    }

    @Test
    void validate_fieldsHasValidValues_noErrorReturned() {
        CommentCreateRequestDto createRequest = new CommentCreateRequestDto("Good pizza");
        Set<ConstraintViolation<CommentCreateRequestDto>> violations = validator.validate(createRequest);

        assertThat(violations).hasSize(0);
    }

}
