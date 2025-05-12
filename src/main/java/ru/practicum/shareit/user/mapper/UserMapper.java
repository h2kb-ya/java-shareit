package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(CreateUserRequest createRequest) {
        return User.builder()
                .name(createRequest.name())
                .email(createRequest.email())
                .build();
    }

    public User toUser(UpdateUserRequest updateRequest) {
        return User.builder()
                .name(updateRequest.name())
                .email(updateRequest.email())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
