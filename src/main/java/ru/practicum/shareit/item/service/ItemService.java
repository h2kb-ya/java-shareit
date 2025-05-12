package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Item getItemById(Long itemId);

    List<Item> getItemsByUser(Long userId);

    List<Item> findItems(String search);

    List<Item> searchAvailableItems(String searchText);
}
