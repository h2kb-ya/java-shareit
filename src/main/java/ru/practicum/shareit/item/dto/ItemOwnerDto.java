package ru.practicum.shareit.item.dto;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;

public record ItemOwnerDto(
        Long id,
        String name,
        String description,
        boolean available,
        Booking lastBooking,
        Booking nextBooking,
        List<CommentDto> comments
) {

}
