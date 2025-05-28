package ru.practicum.shareit.item.controller;

import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

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
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemCreateRequest createRequest) {
        log.info("Request: POST /items, data: {}, by user: {}", createRequest, userId);
        final Item item = itemMapper.toItem(createRequest);

        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
            @RequestBody ItemUpdateRequest updateRequest) {
        log.info("Request: PATCH /items/{}, data: {}, by user: {}", itemId, updateRequest, userId);
        Item existingItem = itemService.getItemById(itemId);
        itemMapper.updateItem(existingItem, updateRequest);

        return itemMapper.toItemDto(itemService.updateItem(userId, existingItem));
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItemById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
        log.info("Request: GET /items/{}", itemId);

        return itemService.getItemByIdAndOwner(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Request: GET /items for user: {}", userId);

        return itemMapper.toItemDtoList(itemService.getItemsByUser(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Request: GET /items with search text {}", text);

        return itemMapper.toItemDtoList(itemService.searchAvailableItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto sendComment(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
            @RequestBody CommentCreateRequest createRequest) {
        log.info("Request: POST /items/{}/comment, comment: {}", itemId, createRequest);
        final Comment comment = itemMapper.toComment(createRequest);

        return itemMapper.toCommentDto(itemService.sendComment(comment, userId, itemId));
    }
}
