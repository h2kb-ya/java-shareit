package ru.practicum.shareit.item.dto;

public record ItemCreateRequest(
        String name,
        String description,
        Boolean available,
        Long requestId
) {

}
