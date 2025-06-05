package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;

public class ItemCreateRequestDtoValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_fieldsHasInvalidValues_errorReturned() {
        ItemCreateRequestDto createRequest = new ItemCreateRequestDto("", "", null, 0L);
        String nameError = "Name must not be blank";
        String descriptionError = "Description must not be blank";
        String availableError = "Available must not be empty";
        Set<ConstraintViolation<ItemCreateRequestDto>> violations = validator.validate(createRequest);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message").contains(nameError, descriptionError, availableError);
    }

    @Test
    void validate_fieldsHasValidValues_noErrorReturned() {
        ItemCreateRequestDto createRequest = new ItemCreateRequestDto("Pizza", "La Bomba", false, 0L);
        Set<ConstraintViolation<ItemCreateRequestDto>> violations = validator.validate(createRequest);

        assertThat(violations).hasSize(0    );
    }

}
