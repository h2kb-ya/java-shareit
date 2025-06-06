package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService target;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_happyPath_bookingCreated() {
        User booker = userService.getUserById(2L);
        Item item = itemService.getItemById(1L);

        Booking newBooking = new Booking();
        newBooking.setStart(LocalDateTime.now().plusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(2));
        newBooking.setStatus(BookingStatus.WAITING);

        Booking result = target.createBooking(newBooking, booker.getId(), item.getId());

        assertNotNull(result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void createBooking_itemNotAvailable_shouldThrow() {
        User booker = userService.getUserById(1L);
        Item unavailableItem = itemService.getItemById(5L);

        Booking newBooking = new Booking();
        newBooking.setStart(LocalDateTime.now().plusDays(3));
        newBooking.setEnd(LocalDateTime.now().plusDays(4));

        assertThrows(NotAvailableException.class, () ->
                target.createBooking(newBooking, booker.getId(), unavailableItem.getId())
        );
    }

    @Test
    void processBooking_approveBooking_success() {
        Booking booking = target.getBookingByIdForBookerOrItemOwner(4L, 3L);

        Booking result = target.processBooking(booking.getId(), 5L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void processBooking_notOwner_shouldThrow() {
        Booking booking = target.getBookingByIdForBookerOrItemOwner(4L, 3L);

        assertThrows(NotAvailableException.class, () ->
                target.processBooking(booking.getId(), 3L, true)
        );
    }

    @Test
    void getBookingByIdForBookerOrItemOwner_happyPath() {
        Booking booking = target.getBookingByIdForBookerOrItemOwner(1L, 1L);

        assertEquals(1L, booking.getId());
    }

    @Test
    void getBookingByIdForBookerOrItemOwner_notAllowed_shouldThrow() {
        assertThrows(NotAvailableException.class, () ->
                target.getBookingByIdForBookerOrItemOwner(1L, 4L)
        );
    }

    @Test
    void getBookingsByUserIdAndState_allStates_success() {
        List<Booking> bookings = target.getBookingsByUserIdAndState(1L, BookingState.ALL);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsForItemOwner_happyPath() {
        List<Booking> bookings = target.getBookingsForItemOwner(5L);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsForItemOwner_userHasNoItems_returnsEmptyList() {
        List<Booking> bookings = target.getBookingsForItemOwner(4L);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getBookingById_notFound_shouldThrow() {
        assertThrows(NotFoundException.class, () ->
                target.getBookingByIdForBookerOrItemOwner(999L, 1L)
        );
    }
}
