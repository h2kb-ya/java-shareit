package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingCreateRequest(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {

}
