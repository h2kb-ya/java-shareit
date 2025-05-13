package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    List<User> getUsers();
}
