package ru.practicum.shareit.user.repository;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {

    User create(User user);

    User update(User user);

    void delete(Long id);

    Optional<User> findById(Long id);

    boolean existByEmail(String email);

    boolean existsById(Long id);

    List<User> findAll();
}
