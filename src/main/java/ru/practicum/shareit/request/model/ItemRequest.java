package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemRequest {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
