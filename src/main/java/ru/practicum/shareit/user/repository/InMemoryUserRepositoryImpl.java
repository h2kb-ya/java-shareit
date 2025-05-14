package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        final long userId = getUserId();
        user.setId(userId);
        users.put(userId, user);

        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existByEmail(String email) {
        return users.values().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(email::equals);
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private Long getUserId() {
        final long id = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);

        return id + 1;
    }
}
