package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.DataIntegrityViolationException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService target;

    @Autowired
    private UserService userService;

    @Test
    void createItem_happyPath_itemCreated() {
        User owner = userService.getUsers().get(0);
        Item item = new Item(null, "Новый предмет", "Описание предмета", true, null, null);

        Item createdItem = target.createItem(owner.getId(), item, null);

        assertNotNull(createdItem.getId());
        assertEquals(owner.getId(), createdItem.getOwner().getId());
        assertEquals("Новый предмет", createdItem.getName());
    }

    @Test
    void updateItem_happyPath_itemUpdated() {
        User owner = userService.getUsers().get(0);

        List<Item> items = target.getItemsByUser(owner.getId());
        if (items.isEmpty()) return;

        Item item = items.get(0);
        item.setName("Обновленное имя");
        item.setDescription("Обновленное описание");
        item.setAvailable(!item.isAvailable());

        Item updated = target.updateItem(owner.getId(), item);

        assertEquals(item.getName(), updated.getName());
        assertEquals(item.getDescription(), updated.getDescription());
        assertEquals(item.isAvailable(), updated.isAvailable());
    }

    @Test
    void updateItem_shouldThrow_whenUserNotOwner() {
        User owner = userService.getUsers().get(0);
        User notOwner = userService.getUsers().get(1);

        List<Item> items = target.getItemsByUser(owner.getId());
        if (items.isEmpty()) return;

        Item item = items.get(0);
        item.setName("Имя не владельца");

        assertThrows(DataIntegrityViolationException.class,
                () -> target.updateItem(notOwner.getId(), item));
    }

    @Test
    void getItemDetailsForOwner_happyPath_itemOwnerDtoReturned() {
        User owner = userService.getUsers().get(0);
        List<Item> items = target.getItemsByUser(owner.getId());
        if (items.isEmpty()) return;

        Item item = items.get(0);
        ItemOwnerDto dto = target.getItemDetailsForOwner(item.getId(), owner.getId());

        assertNotNull(dto);
        assertEquals(item.getId(), dto.id());
    }

    @Test
    void sendComment_happyPath_commentCreated() {
        User user = userService.getUsers().get(0);
        List<Item> items = target.getItemsByUser(user.getId());

        if (items.isEmpty()) return;

        Item item = items.get(0);

        Comment comment = new Comment();
        comment.setText("Отличный предмет!");

        Comment savedComment = target.sendComment(comment, user.getId(), item.getId());

        assertNotNull(savedComment.getId());
        assertEquals(user.getId(), savedComment.getAuthor().getId());
        assertEquals(item.getId(), savedComment.getItem().getId());
    }

    @Test
    void sendComment_shouldThrow_whenBookingNotCompleted() {
        User user = userService.getUsers().get(0);
        List<Item> items = target.getItemsByUser(user.getId());

        if (items.isEmpty()) return;

        Item item = items.get(0);

        Comment comment = new Comment();
        comment.setText("Попытка комментировать без завершенного бронирования");

        assertThrows(CommentNotAllowedException.class,
                () -> target.sendComment(comment, user.getId(), item.getId()));
    }

    @Test
    void searchAvailableItems_happyPath_itemsFound() {
        List<Item> result = target.searchAvailableItems("бритва");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void searchAvailableItems_blankText_emptyListReturned() {
        List<Item> result = target.searchAvailableItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsByItemRequestIds_happyPath_itemsReturned() {
        List<Item> items = target.getItemsByItemRequestIds(List.of(1L, 2L));
        assertNotNull(items);
    }

    @Test
    void getItemsByItemRequestIds_nullOrEmpty_emptyListReturned() {
        assertTrue(target.getItemsByItemRequestIds(null).isEmpty());
        assertTrue(target.getItemsByItemRequestIds(List.of()).isEmpty());
    }
}
