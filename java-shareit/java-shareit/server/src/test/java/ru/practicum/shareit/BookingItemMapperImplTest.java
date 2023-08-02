package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingItemMapper;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingItemMapperImplTest {

    private final BookingItemMapper mapper = new BookingItemMapper();

    @Test
    void toDto_WithValidBooking_ReturnsBookingDtoItem() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setStatus(StatusEnum.WAITING);
        booking.setId(1L);
        booking.setStart(now);
        booking.setEnd(now.plusHours(2));

        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);

        BookingDtoItem result = mapper.toDto(booking);

        assertEquals(StatusEnum.WAITING, result.getStatus());
        assertEquals(1L, result.getId());
        assertEquals(now, result.getStart());
        assertEquals(now.plusHours(2), result.getEnd());
        assertEquals(2L, result.getBookerId());
    }
}

