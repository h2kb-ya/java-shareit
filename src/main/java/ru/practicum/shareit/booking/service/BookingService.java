package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

public interface BookingService {

    Booking createBooking(Booking booking, Long bookerId, Long itemId);

    Booking processBooking(Long bookingId, Long ownerId, boolean approved);

    Booking getBookingById(Long bookingId);

    List<Booking> getBookingsByUserIdAndState(Long userId, BookingState state);
}
