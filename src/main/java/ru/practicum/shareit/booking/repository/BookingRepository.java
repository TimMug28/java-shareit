package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long id);

    //ALL
    @Query("select b from Booking b where b.booker = ?1 order by b.start DESC")
    List<Booking> findAllBookingsByBooker(User user);

    //FUTURE
    List<Booking> findAllByBookerAndStartAfterOrderByStart(User user, LocalDateTime localDateTime);


    // REJECTED /WAITING
    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?3 " +
            "order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
            User user, StatusEnum statusEnum);

    //PAST
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    //CURRENT
    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.start < ?2 and b.end > ?3 " +
            "order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime, LocalDateTime localDateTime2);
}