package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

public record BookItemRequestDto(
        long itemId,

        @FutureOrPresent(message = "Start must be in future or present")
        LocalDateTime start,

        @Future(message = "End must be in future")
        LocalDateTime end
) {

}
