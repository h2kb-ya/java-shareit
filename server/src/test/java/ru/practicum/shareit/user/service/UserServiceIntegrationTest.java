package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService target;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_happyPath_userCreated() {
        User newUser = new User(null, "Караи", "karai@footclan.com");

        User createdUser = target.createUser(newUser);

        assertNotNull(createdUser.getId());
        assertEquals(newUser.getName(), createdUser.getName());
        assertEquals(newUser.getEmail(), createdUser.getEmail());

        User dbUser = userRepository.findById(createdUser.getId()).orElseThrow();
        assertEquals(createdUser, dbUser);
    }

    @Test
    void createUser_emailAlreadyExists_errorReturned() {
        long initialUserCount = userRepository.count();
        String masterEmail = "splinter@tmnt.com";
        User newUserWhitDuplicateEmail = new User(null, "Шпион ноги", masterEmail);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> target.createUser(newUserWhitDuplicateEmail)
        );

        assertEquals("User with email " + masterEmail + " already exists", e.getMessage());
        assertEquals(initialUserCount, userRepository.count());
    }

    @Test
    void updateUser_happyPath_userUpdated() {
        User existingUser = userRepository.findAll().getFirst();
        User userForUpdate = new User(existingUser.getId(), "Леонардо (Лидер)", "newleo@tmnt.com");

        User updatedUser = target.updateUser(userForUpdate);

        assertEquals(userForUpdate.getName(), updatedUser.getName());
        assertEquals(userForUpdate.getEmail(), updatedUser.getEmail());

        User dbUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals(updatedUser, dbUser);
    }

    @Test
    void updateUser_emailAlreadyExist_errorReturned() {
        String brotherEmail = "mike@tmnt.com";
        User existingUser = userRepository.findAll().getFirst();
        User userForUpdate = new User(existingUser.getId(), "Леонардо (Лидер)", brotherEmail);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> target.createUser(userForUpdate)
        );

        assertEquals("User with email " + brotherEmail + " already exists", e.getMessage());

        existingUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertNotEquals(existingUser, userForUpdate);
    }

    @Test
    void deleteUser_happyPath_userDeleted() {
        long initialUserCount = userRepository.count();
        User user = new User(null, "Шпион ноги", "spy@footclan.com");
        User createdUser = target.createUser(user);

        assertEquals(initialUserCount + 1, userRepository.count());

        target.deleteUser(createdUser.getId());

        assertEquals(initialUserCount, userRepository.count());
        assertFalse(userRepository.existsById(createdUser.getId()));
    }

    @Test
    void getUserById_happyPath_userReturned() {
        User existingUser = userRepository.findAll().getFirst();
        User foundUser = target.getUserById(existingUser.getId());

        assertEquals(existingUser.getId(), foundUser.getId());
        assertEquals(existingUser.getName(), foundUser.getName());
        assertEquals(existingUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void getUsers_happyPath_usersReturned() {
        List<User> users = target.getUsers();

        assertEquals(8, users.size());
    }
}
