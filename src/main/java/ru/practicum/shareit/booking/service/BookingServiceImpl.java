package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
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
        Long currentOwnerId = booking.getItem().getOwner().getId();

        if (!currentOwnerId.equals(ownerId)) {
            throw new NotAvailableException("User " + ownerId + " is not owner for item " + booking.getItem().getId());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated: {} new status {}", updatedBooking, updatedBooking.getStatus());

        return updatedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        log.debug("Getting booking by id: {}", bookingId);

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking " + bookingId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserIdAndState(Long userId, BookingState state) {
        log.debug("Getting bookings by userId {} and state {}", userId, state);

        return switch (state) {
            case CURRENT -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case PAST -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.CANCELLED);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
    }
}
