package ru.practicum.shareit.item.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        final long itemId = getItemId();
        item.setId(itemId);
        items.put(itemId, item);

        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAvailableByNameOrDescriptionContaining(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(i -> StringUtils.containsIgnoreCase(text, i.getName())
                        || StringUtils.containsIgnoreCase(text, i.getDescription()))
                .collect(Collectors.toList());
    }

    private Long getItemId() {
        final long id = items.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        return id + 1;
    }
}
