package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestAndAnswersDto(
        Long id,
        String description,
        Long requestorId,
        LocalDateTime created,
        List<ItemRequestAnswerDto> items
) {

}
