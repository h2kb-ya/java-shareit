package ru.practicum.shareit.item.dto;

public record ItemDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long ownerId,
        Long requestId
) {

}
