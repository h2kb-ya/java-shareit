package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        String name,
        @Email(message = "Illegal format of email address")
        String email
) {

}
