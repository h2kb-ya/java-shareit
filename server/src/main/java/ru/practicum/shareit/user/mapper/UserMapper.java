package ru.practicum.shareit.user.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateRequestDto userCreateRequestDto);

    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);
}
