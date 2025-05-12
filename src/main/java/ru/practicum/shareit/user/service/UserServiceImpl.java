package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
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
    public User createUser(User user) {
        log.debug("Creating user: {}", user);
        if (!userRepository.existByEmail(user.getEmail())) {
            User createdUser = userRepository.create(user);
            log.info("User created: {}", createdUser);

            return createdUser;
        } else {
            log.warn("User with email {} already exists", user.getEmail());
            throw new DataIntegrityViolationException("User with email " + user.getEmail() + " already exists");
        }
    }

    @Override
    public User updateUser(User user) {
        log.debug("Updating user: {}", user);
        final User currentUser = getUserById(user.getId());

        if (user.getEmail() != null && !user.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existByEmail(user.getEmail())) {
                log.warn("User with email {} already exists", user.getEmail());
                throw new DataIntegrityViolationException("User with email " + user.getEmail() + " already exists");
            }

            currentUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        log.info("User updated: {}", currentUser);

        return userRepository.update(currentUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.delete(userId);
            log.info("User deleted: {}", userId);
        }
    }

    @Override
    public User getUserById(Long userId) {
        log.debug("Getting user by id: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
    }
}
