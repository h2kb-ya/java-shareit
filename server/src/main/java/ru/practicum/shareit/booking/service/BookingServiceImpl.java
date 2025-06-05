package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking createBooking(Booking booking, Long bookerId, Long itemId) {
        log.debug("Creating booking: {}", booking);
        User user = userService.getUserById(bookerId);
        Item item = itemService.getItemById(itemId);

        if (!item.isAvailable()) {
            throw new NotAvailableException("Item " + itemId + " is not available for booking");
        }

        booking.setBooker(user);
        booking.setItem(item);

        Booking createdBooking = bookingRepository.save(booking);
        log.info("Booking created: {}", createdBooking);

        return createdBooking;
    }

    @Override
    @Transactional
    public Booking processBooking(Long bookingId, Long ownerId, boolean approved) {
        log.debug("Processing booking: {} by owner {} approved {}", bookingId, ownerId, approved);
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();

        if (!itemService.isItemOwner(ownerId, item)) {
            throw new NotAvailableException("User " + ownerId + " is not owner for item " + booking.getItem().getId());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated: {} new status {}", updatedBooking, updatedBooking.getStatus());

        return updatedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingByIdForBookerOrItemOwner(Long bookingId, Long userId) {
        log.debug("Getting booking by id: {}", bookingId);
        Booking booking = getBookingById(bookingId);
        Long itemId = booking.getItem().getId();

        if (!isUserAllowedToAccessBooking(userId, booking)) {
            throw new NotAvailableException(
                    "User " + userId + " is not owner for item " + itemId + " or for booking " + bookingId);
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserIdAndState(Long userId, BookingState state) {
        log.debug("Getting bookings by userId {} and state {}", userId, state);
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByUser(userId, now);
            case WAITING -> bookingRepository.findWaitingBookingsByUser(userId);
            case PAST -> bookingRepository.findPastBookingsByUser(userId, now);
            case REJECTED -> bookingRepository.findRejectedBookingsByUser(userId);
            case FUTURE -> bookingRepository.findFutureBookingsByUser(userId, now);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
    }

    @Override
    public List<Booking> getBookingsForItemOwner(long ownerId) {
        userService.getUserById(ownerId);
        List<Long> itemIds = itemService.getItemsByUser(ownerId).stream().map(Item::getId).toList();

        if (itemIds.isEmpty()) {
            return List.of();
        }

        return bookingRepository.findAllByItemIdIn(itemIds);
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking " + bookingId + " not found"));
    }

    private boolean isBooker(Long userId, Booking booking) {
        return booking.getBooker().getId().equals(userId);
    }

    private boolean isUserAllowedToAccessBooking(Long userId, Booking booking) {
        return isBooker(userId, booking) || itemService.isItemOwner(userId, booking.getItem());
    }
}
