package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    ItemDto createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    ItemOwnerDto getItemByIdAndOwner(Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<Item> getItemsByUser(Long userId);

    List<Item> searchAvailableItems(String searchText);

    Comment sendComment(Comment comment, Long userId, Long itemId);
}
