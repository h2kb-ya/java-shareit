package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public record BookingDto(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        ItemDto item,
        UserDto booker,
        BookingStatus status
) {

}
