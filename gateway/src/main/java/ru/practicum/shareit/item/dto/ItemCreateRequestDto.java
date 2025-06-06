package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCreateRequestDto(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Description must not be blank")
        String description,

        @NotNull(message = "Available must not be empty")
        Boolean available,

        Long requestId
) {

}
