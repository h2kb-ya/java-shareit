package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(
        @NotBlank(message = "Comment text must not be blank")
        String text
) {

}
