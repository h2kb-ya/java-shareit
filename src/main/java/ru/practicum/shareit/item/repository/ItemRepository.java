package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item item);

    void delete(Long id);

    Optional<Item> findById(Long id);

    boolean existsById(Long id);

    List<Item> findAll();

    List<Item> findAllByUserId(Long userId);

    List<Item> findAvailableByNameOrDescriptionContaining(String searchText);
}
