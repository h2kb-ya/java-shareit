package ru.practicum.shareit.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Request: GET /users/{}", userId);

        return userMapper.toUserDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Request: GET /users");

        return userMapper.toUserDtoList(userService.getUsers());
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateRequestDto createRequest) {
        log.info("Request: POST /users, data: {}", createRequest);
        final User user = userMapper.toUser(createRequest);

        return userMapper.toUserDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId, @RequestBody UserUpdateRequest updateRequest) {
        log.info("Request: PATCH /users/{}, data: {}", userId, updateRequest);
        final User existingUser = userService.getUserById(userId);
        userMapper.updateUser(existingUser, updateRequest);

        return userMapper.toUserDto(userService.updateUser(existingUser));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("Request: DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }
}
