package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(
            Long userId,
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );
}
