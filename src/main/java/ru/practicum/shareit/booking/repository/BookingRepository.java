package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL для Booker
    @Query("select b from Booking b where b.booker = ?1 order by b.start DESC")
    List<Booking> findAllBookingsByBooker(User user);

    //FUTURE для Booker
    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    // REJECTED and WAITING для Booker
    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(
            User user, StatusEnum statusEnum);

    //PAST для Booker
    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    //CURRENT для Booker
    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?3 " +
            "order by b.start DESC")
    List<Booking> findAllBookingsForBookerWithStartAndEnd(
            User user, LocalDateTime localDateTime, LocalDateTime localDateTime2);

    //-----------------
    //ALL для Owner
    @Query("select b from Booking b where b.item.owner = ?1 order by b.start DESC")
    List<Booking> findAllBookingsByItem_Owner(User user);

    //FUTURE для Owner
    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    // REJECTED and WAITING для Owner

    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
            User user, StatusEnum statusEnum);

    //PAST для Owner
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    //CURRENT для Owner
    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.start < ?2 and b.end > ?3 " +
            "order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime, LocalDateTime localDateTime2);
}