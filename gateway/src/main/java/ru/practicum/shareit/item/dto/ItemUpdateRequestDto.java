package ru.practicum.shareit.item.dto;

public record ItemUpdateRequestDto(
        String name,
        String description,
        Boolean available
) {

}
