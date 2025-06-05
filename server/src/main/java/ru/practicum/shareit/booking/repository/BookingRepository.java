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

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(
            Long userId,
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :userId
            AND b.start <= :now
            AND b.end >= :now
            AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
            ORDER BY b.start DESC
            """)
    List<Booking> findCurrentBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :userId
            AND b.status = ru.practicum.shareit.booking.model.BookingStatus.WAITING
            ORDER BY b.start DESC
            """)
    List<Booking> findWaitingBookingsByUser(@Param("userId") Long userId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :userId
            AND b.end < :now
            ORDER BY b.start DESC
            """)
    List<Booking> findPastBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :userId
            AND b.status = ru.practicum.shareit.booking.model.BookingStatus.REJECTED
            ORDER BY b.start DESC
            """)
    List<Booking> findRejectedBookingsByUser(@Param("userId") Long userId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :userId
            AND b.start > :now
            ORDER BY b.start DESC
            """)
    List<Booking> findFutureBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
            AND b.end < :now
            ORDER BY b.end DESC
            LIMIT 1
            """)
    Booking findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
            AND b.start > :now
            ORDER BY b.end DESC
            LIMIT 1
            """)
    Booking findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
}
