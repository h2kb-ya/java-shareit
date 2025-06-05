package ru.practicum.shareit.item;

import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
            @RequestBody @Valid ItemCreateRequestDto createRequest) {
        return itemClient.createItem(userId, createRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody ItemUpdateRequestDto updateRequest) {
        return itemClient.updateItem(userId, itemId, updateRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable("itemId") long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam String text) {
        return itemClient.searchAvailableItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable("itemId") long itemId, @RequestBody @Valid CommentCreateRequestDto createRequest) {

        return itemClient.addComment(userId, itemId, createRequest);
    }
}
