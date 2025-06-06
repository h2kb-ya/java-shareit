package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String text,
        Long itemId,
        Long authorId,
        String authorName,
        LocalDateTime created
) {

}
