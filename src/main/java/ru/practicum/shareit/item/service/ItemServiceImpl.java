package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item createItem(Long userId, Item item) {
        log.debug("Creating an item {} by user {}", item, userId);
        final User owner = userService.getUserById(userId);
        item.setOwner(owner);
        Item createdItem = itemRepository.create(item);
        log.info("Item created: {}", createdItem);

        return createdItem;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        log.debug("Updating an item {} by user {}", item, userId);
        userService.getUserById(userId);
        Optional<Item> itemToUpdateOptional = itemRepository.findById(item.getId());

        if (itemToUpdateOptional.isPresent() && checkItemOwner(userId, itemToUpdateOptional.get())) {
            Item itemToUpdate = itemToUpdateOptional.get();
            itemToUpdate.setName(item.getName());
            itemToUpdate.setDescription(item.getDescription());
            itemToUpdate.setAvailable(item.isAvailable());

            Item updatedItem = itemRepository.update(itemToUpdate);
            log.info("Item updated: {}", updatedItem);

            return updatedItem;
        }

        throw new DataIntegrityViolationException("User " + userId + " does not an owner for item " + item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        log.debug("Getting item by id: {}", itemId);

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        log.debug("Getting items by user: {}", userId);
        userService.getUserById(userId);

        return itemRepository.findAllByUserId(userId);
    }

    @Override
    public List<Item> findItems(String search) {
        return List.of();
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        log.debug("Searching available items by text {}", text);

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findAvailableByNameOrDescriptionContaining(text);
    }

    private boolean checkItemOwner(Long userId, Item item) {
        return item.getOwner().getId().equals(userId);
    }
}
