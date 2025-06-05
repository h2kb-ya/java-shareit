package ru.practicum.shareit.request.dto;

public record ItemRequestAnswerDto(
        Long itemId,
        String name,
        String description,
        Long ownerId
) {

}
