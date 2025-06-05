package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

public class BookItemRequestDtoValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_fieldsHasInvalidValues_errorReturned() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        BookItemRequestDto book = new BookItemRequestDto(1, yesterday, yesterday);
        String startError = "Start must be in future or present";
        String endError = "End must be in future";

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(book);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message").contains(startError, endError);
    }

    @Test
    void validate_fieldsHasValidValues_noErrorReturned() {
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        BookItemRequestDto book = new BookItemRequestDto(1, now, tomorrow);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(book);

        assertThat(violations).hasSize(0);
    }
}
