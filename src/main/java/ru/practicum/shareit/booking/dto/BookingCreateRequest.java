package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import ru.practicum.shareit.booking.validation.ValidBookingDates;

@ValidBookingDates
public record BookingCreateRequest(
        @NotNull(message = "ItemId must not be empty")
        Long itemId,

        @NotNull(message = "Start must not be empty")
        LocalDateTime start,

        @NotNull(message = "End must not be empty")
        LocalDateTime end
) {

}
