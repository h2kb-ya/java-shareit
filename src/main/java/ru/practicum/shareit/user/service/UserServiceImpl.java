package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        log.debug("Creating user: {}", user);
        if (!userRepository.existsByEmail(user.getEmail())) {
            User createdUser = userRepository.save(user);
            log.info("User created: {}", createdUser);

            return createdUser;
        } else {
            log.warn("User with email {} already exists", user.getEmail());
            throw new DataIntegrityViolationException("User with email " + user.getEmail() + " already exists");
        }
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        log.debug("Updating user: {}", user);
        final User currentUser = getUserById(user.getId());

        if (user.getEmail() != null && !user.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                log.warn("User with email {} already exists", user.getEmail());
                throw new DataIntegrityViolationException("User with email " + user.getEmail() + " already exists");
            }

            currentUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        log.info("User updated: {}", currentUser);

        return userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("User deleted: {}", userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.debug("Getting user by id: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        log.debug("Getting all users");

        return userRepository.findAll();
    }
}
