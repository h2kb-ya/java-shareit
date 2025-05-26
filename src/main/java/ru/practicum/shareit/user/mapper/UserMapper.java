package ru.practicum.shareit.user.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequest updateUserRequest);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);
}
