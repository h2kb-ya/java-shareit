package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;

public record CommentCreateRequestDto(
        @NotNull(message = "Comment text must not be null")
        String text
) {

}
