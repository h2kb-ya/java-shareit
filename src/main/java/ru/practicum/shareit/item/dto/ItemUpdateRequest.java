package ru.practicum.shareit.item.dto;

public record ItemUpdateRequest(
        String name,
        String description,
        Boolean available
) {

}
