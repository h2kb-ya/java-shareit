package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Log4j2
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        log.info("Request: GET /bookings/{}", bookingId);

        return bookingMapper.toBookingDto(bookingService.getBookingById(bookingId));
    }

    @GetMapping
    public List<BookingDto> getBookingsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Request: GET /bookings by userId {} and state {}", userId, state);

        return bookingMapper.toBookingDtoList(bookingService.getBookingsByUserIdAndState(userId, state));
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
            @RequestBody @Valid BookingCreateRequest createRequest) {
        log.info("Request: POST /bookings/{} by booker {}", createRequest, bookerId);
        final Booking booking = bookingMapper.toBooking(createRequest);

        return bookingMapper.toBookingDto(bookingService.createBooking(booking, bookerId, createRequest.itemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto processBooking(@RequestHeader(USER_ID_HEADER) Long ownerId, @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Request: PUT /bookings/{}?approved={} by booker {}", bookingId, approved, ownerId);

        return bookingMapper.toBookingDto(bookingService.processBooking(bookingId, ownerId, approved));
    }
}
