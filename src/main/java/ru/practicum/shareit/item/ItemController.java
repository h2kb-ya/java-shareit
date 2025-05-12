package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody CreateItemRequest createRequest) {
        log.info("Request: POST /items, data: {}, by user: {}", createRequest, userId);
        final Item item = ItemMapper.toItem(createRequest);

        return ItemMapper.toDto(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
            @RequestBody UpdateItemRequest updateRequest) {
        log.info("Request: PATCH /items/{}, data: {}, by user: {}", itemId, updateRequest, userId);
        Item item = ItemMapper.toItem(updateRequest);
        item.setId(itemId);

        return ItemMapper.toDto(itemService.updateItem(userId, item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("Request: GET /items/{}", itemId);

        return ItemMapper.toDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Request: GET /items for user: {}", userId);

        return ItemMapper.toDto(itemService.getItemsByUser(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Request: GET /items with search text {}", text);

        return ItemMapper.toDto(itemService.searchAvailableItems(text));
    }
}
