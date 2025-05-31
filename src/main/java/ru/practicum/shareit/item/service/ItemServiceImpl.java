package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item createItem(Long userId, Item item) {
        log.debug("Creating an item {} by user {}", item, userId);
        final User owner = userService.getUserById(userId);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);
        log.info("Item created: {}", createdItem);

        return createdItem;
    }

    @Override
    @Transactional
    public Item updateItem(Long userId, Item item) {
        log.debug("Updating an item {} by user {}", item, userId);
        userService.getUserById(userId);
        Optional<Item> itemToUpdateOptional = itemRepository.findById(item.getId());

        if (itemToUpdateOptional.isEmpty()) {
            throw new NotFoundException("Item " + item.getId() + " not found");
        }

        if (isItemOwner(userId, itemToUpdateOptional.get())) {
            Item itemToUpdate = itemToUpdateOptional.get();
            itemToUpdate.setName(item.getName());
            itemToUpdate.setDescription(item.getDescription());
            itemToUpdate.setAvailable(item.isAvailable());

            Item updatedItem = itemRepository.save(itemToUpdate);
            log.info("Item updated: {}", updatedItem);

            return updatedItem;
        }

        throw new DataIntegrityViolationException("User " + userId + " does not an owner for item " + item.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemOwnerDto getItemDetailsForOwner(Long itemId, Long userId) {
        log.debug("Getting item by id: {}", itemId);
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findCommentsByItemId(itemId);
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (isItemOwner(userId, item)) {
            lastBooking = bookingRepository.findLastBooking(itemId, LocalDateTime.now());
            nextBooking = bookingRepository.findNextBooking(itemId, LocalDateTime.now());
        }

        return itemMapper.toItemOwnerDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getItemsByUser(Long userId) {
        log.debug("Getting items by user: {}", userId);
        userService.getUserById(userId);

        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        log.debug("Searching available items by text {}", text);

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(text);
    }

    @Override
    @Transactional
    public Comment sendComment(Comment comment, Long userId, Long itemId) {
        log.debug("Sending comment {} by user {} to item {}", comment, userId, itemId);
        if (!hasUserCompletedBooking(userId, itemId)) {
            throw new CommentNotAllowedException(
                    "User " + userId + " didn't use item " + itemId + " or booking is not completed yet");
        }

        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        comment.setAuthor(user);
        comment.setItem(item);

        Comment createdComment = commentRepository.save(comment);
        log.info("Comment created: {}", createdComment);

        return createdComment;
    }

    @Override
    @Transactional
    public Item getItemById(Long itemId) {
        log.debug("Getting item by id {}", itemId);

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
    }

    @Override
    public boolean isItemOwner(Long userId, Item item) {
        return item.getOwner().getId().equals(userId);
    }

    private boolean hasUserCompletedBooking(Long userId, Long itemId) {
        return bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId,
                itemId,
                LocalDateTime.now(),
                BookingStatus.APPROVED
        );
    }
}
