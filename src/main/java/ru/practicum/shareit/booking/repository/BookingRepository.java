package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
    SELECT b FROM Booking b
    WHERE b.item.id = :itemId
    AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
    AND b.end < :currentTime
    ORDER BY b.end DESC
    LIMIT 1""")
    Booking findLastBooking(
            @Param("itemId") Long itemId,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("""
    SELECT b FROM Booking b
    WHERE b.item.id = :itemId
    AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
    AND b.start > :currentTime
    ORDER BY b.end DESC
    LIMIT 1""")
    Booking findNextBooking(
            @Param("itemId") Long itemId,
            @Param("currentTime") LocalDateTime currentTime);
}
