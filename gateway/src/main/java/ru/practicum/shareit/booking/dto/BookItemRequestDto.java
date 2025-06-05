package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

public record BookItemRequestDto(
        long itemId,

        @FutureOrPresent
        LocalDateTime start,

        @Future
        LocalDateTime end
) {

}
