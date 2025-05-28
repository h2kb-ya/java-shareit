package ru.practicum.shareit.booking.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    @Mapping(target = "start", source = "createRequest.start")
    @Mapping(target = "end", source = "createRequest.end")
    Booking toBooking(BookingCreateRequest createRequest);

    List<BookingDto> toBookingDtoList(List<Booking> bookings);
}
