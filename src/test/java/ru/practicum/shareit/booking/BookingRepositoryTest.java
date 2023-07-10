package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User booker;
    User owner;
    Item item;
    Booking booking1;
    Booking booking2;
    Booking booking3;
    LocalDateTime startDate;
    PageRequest pageRequest;

    @BeforeEach
    void start() {
        int page = 0 / 10;
        pageRequest = PageRequest.of(page, 10);

        booker = User.builder()
                .name("booker")
                .email("booker@yandex.ru")
                .build();

        userRepository.save(booker);
        owner = User.builder()
                .name("owner")
                .email("owner@yandex.ru")
                .build();
        userRepository.save(owner);
        item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(null)
                .owner(owner)
                .build();
        itemRepository.save(item);

        startDate = LocalDateTime.now();

        booking1 = Booking.builder()
                .start(startDate.minusHours(2))
                .end(startDate.minusHours(1))
                .booker(booker)
                .item(item)
                .status(StatusEnum.APPROVED)
                .build();
        bookingRepository.save(booking1);

        booking2 = Booking.builder()
                .start(startDate.plusHours(1))
                .end(startDate.plusHours(2))
                .booker(booker)
                .item(item)
                .status(StatusEnum.REJECTED)
                .build();
        bookingRepository.save(booking2);

        booking3 = Booking.builder()
                .start(startDate.plusHours(3))
                .end(startDate.plusHours(4))
                .booker(booker)
                .status(StatusEnum.APPROVED)
                .build();
        bookingRepository.save(booking3);
    }

    @Test
    void testFindAllBookingsByBooker() {
        List<Booking> bookingList = bookingRepository.findAllBookingsByBooker(booker, pageRequest);

        assertNotNull(bookingList);
        assertEquals(3, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(1).getId());
        assertEquals(booking1.getId(), bookingList.get(2).getId());
    }

    @Test
    void testFindAllByBookerAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(booker, startDate, pageRequest);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking3.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    void testFindAllByBookerAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(booker, StatusEnum.APPROVED, pageRequest);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking3.getId(), bookings.get(0).getId());
        assertEquals(booking1.getId(), bookings.get(1).getId());
    }

    @Test
    void testFindAllByBookerAndEndIsBeforeOrderByStartDesc() {
        LocalDateTime endDate = LocalDateTime.now();

        Booking booking1 = Booking.builder()
                .start(endDate.minusHours(2))
                .end(endDate.minusHours(1))
                .booker(booker)
                .status(StatusEnum.APPROVED)
                .build();
        bookingRepository.save(booking1);

        Booking booking2 = Booking.builder()
                .start(endDate.plusHours(1))
                .end(endDate.plusHours(2))
                .booker(booker)
                .status(StatusEnum.APPROVED)
                .build();
        bookingRepository.save(booking2);

        Booking booking3 = Booking.builder()
                .start(endDate.plusHours(3))
                .end(endDate.plusHours(4))
                .booker(booker)
                .status(StatusEnum.APPROVED)
                .build();
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker, endDate, pageRequest);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindAllBookingsByItemOwner() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItem_Owner(owner, pageRequest);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
        assertEquals(booking1.getId(), bookings.get(1).getId());
    }

    @Test
    void testFindAllByItem_OwnerAndStartIsAfterOrderByStartDesc() {
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);

        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(owner, localDateTime, pageRequest);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
    }

    @Test
    void testFindAllBookingsForBookerWithStartAndEnd() {
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(2);
        LocalDateTime endDateTime = LocalDateTime.now().minusDays(1);

        List<Booking> bookingList = bookingRepository.findAllBookingsForBookerWithStartAndEnd(booker, startDateTime, endDateTime, pageRequest);

        assertNotNull(bookingList);
        assertEquals(0, bookingList.size());
    }

    @Test
    void testFindAllByItem_OwnerAndStatusEqualsOrderByStartDesc() {
        booking2.setStatus(StatusEnum.CANCELED);

        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(owner, StatusEnum.APPROVED, pageRequest);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
    }

    @Test
    void testFindAllByItem_OwnerAndEndIsBeforeOrderByStartDesc() {
        LocalDateTime endDate = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(owner, endDate, pageRequest);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }


    @Test
    void testFindAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime localDateTime1 = LocalDateTime.now().minusHours(2);
        LocalDateTime localDateTime2 = LocalDateTime.now().plusHours(2);

        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(owner, localDateTime2, localDateTime1, pageRequest);

        assertNotNull(bookingList);
        assertEquals(2, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(1).getId());
    }
}


