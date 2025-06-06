package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    private final Long userId = 1L;
    private final Long bookingId = 10L;
    private final Long itemId = 5L;

    private final Booking booking = new Booking();
    private final BookingDto bookingDto = new BookingDto(
            bookingId,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            new ItemDto(itemId, "item", "desc", true, null),
            new UserDto(userId, "user", "user@email.com"),
            null
    );

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        when(bookingService.getBookingByIdForBookerOrItemOwner(bookingId, userId)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.id()));

        verify(bookingService).getBookingByIdForBookerOrItemOwner(bookingId, userId);
        verify(bookingMapper).toBookingDto(booking);
    }

    @Test
    void getBookingsByUser_shouldReturnList() throws Exception {
        when(bookingService.getBookingsByUserIdAndState(userId, BookingState.ALL)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoList(List.of(booking))).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.id()));

        verify(bookingService).getBookingsByUserIdAndState(userId, BookingState.ALL);
        verify(bookingMapper).toBookingDtoList(List.of(booking));
    }

    @Test
    void getBookingsForItemOwner_shouldReturnList() throws Exception {
        when(bookingService.getBookingsForItemOwner(userId)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDtoList(List.of(booking))).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.id()));

        verify(bookingService).getBookingsForItemOwner(userId);
        verify(bookingMapper).toBookingDtoList(List.of(booking));
    }

    @Test
    void createBooking_shouldReturnBookingDto() throws Exception {
        BookingCreateRequest createRequest = new BookingCreateRequest(itemId,
                bookingDto.start(), bookingDto.end());

        when(bookingMapper.toBooking(any(BookingCreateRequest.class))).thenReturn(booking);
        when(bookingService.createBooking(any(Booking.class), anyLong(), anyLong())).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.id()));

        verify(bookingMapper).toBooking(createRequest);
        verify(bookingService).createBooking(booking, userId, itemId);
        verify(bookingMapper).toBookingDto(booking);
    }

    @Test
    void processBooking_shouldReturnUpdatedBooking() throws Exception {
        boolean approved = true;

        when(bookingService.processBooking(bookingId, userId, approved)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.id()));

        verify(bookingService).processBooking(bookingId, userId, approved);
        verify(bookingMapper).toBookingDto(booking);
    }
}
