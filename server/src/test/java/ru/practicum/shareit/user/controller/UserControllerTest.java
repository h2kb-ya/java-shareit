package ru.practicum.shareit.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private final long userId = 1L;
    private final long wrongUserId = 999;
    private final UserDto userDto = new UserDto(userId, "Леонардо", "leo@tmnt.com");
    private final User user = new User(userId, "Леонардо", "leo@tmnt.com");

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.id()))
                .andExpect(jsonPath("$.name").value(userDto.name()))
                .andExpect(jsonPath("$.email").value(userDto.email()));

        verify(userService).getUserById(userId);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getUsers_shouldReturnAllUsers() throws Exception {
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        when(userService.getUsers()).thenReturn(users);
        when(userMapper.toUserDtoList(users)).thenReturn(userDtos);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].name").value(userDto.name()))
                .andExpect(jsonPath("$[0].email").value(userDto.email()));

        verify(userService).getUsers();
        verify(userMapper).toUserDtoList(users);
    }

    @Test
    void createUser_shouldCreateNewUser() throws Exception {
        UserCreateRequestDto createRequest = new UserCreateRequestDto("Леонардо", "leo@tmnt.com");

        when(userMapper.toUser(any(UserCreateRequestDto.class))).thenReturn(user);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.id()))
                .andExpect(jsonPath("$.name").value(userDto.name()))
                .andExpect(jsonPath("$.email").value(userDto.email()));

        verify(userMapper).toUser(createRequest);
        verify(userService).createUser(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void updateUser_shouldUpdateUser() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("Леонардо (Лидер)", "newleo@tmnt.com");
        User updatedUser = new User(1L, "Леонардо (Лидер)", "newleo@tmnt.com");
        UserDto updatedDto = new UserDto(1L, "Леонардо (Лидер)", "newleo@tmnt.com");

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserDto(any(User.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.id()))
                .andExpect(jsonPath("$.name").value(updatedDto.name()))
                .andExpect(jsonPath("$.email").value(updatedDto.email()));
    }

    @Test
    void deleteUser_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserNotExists() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/{userId}", wrongUserId))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(wrongUserId);
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserNotExists() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("Новое имя", "new@email.com");

        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/{userId}", wrongUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(wrongUserId);
    }
}
