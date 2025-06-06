package ru.practicum.shareit.item.dto;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;

public record ItemOwnerDto(
        Long id,
        String name,
        String description,
        boolean available,
        BookingDto lastBooking,
        BookingDto nextBooking,
        List<CommentDto> comments
) {

}
