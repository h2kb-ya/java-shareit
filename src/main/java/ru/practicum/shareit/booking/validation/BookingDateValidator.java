package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;

public class BookingDateValidator implements ConstraintValidator<ValidBookingDates, BookingCreateRequest> {

    @Override
    public boolean isValid(BookingCreateRequest request, ConstraintValidatorContext context) {
        if (request.end().isBefore(LocalDateTime.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("End must be in the future")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }

        if (request.start().isEqual(request.end())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start and end must not be equal")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }

        if (!request.start().isBefore(request.end())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start must be before end")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
