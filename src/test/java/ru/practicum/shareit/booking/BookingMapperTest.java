package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    public void testToDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("вещь");

        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusEnum.APPROVED);

        BookingDto dto = BookingMapper.toDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getItem().getId(), dto.getItemId());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getBooker().getName(), dto.getBooker().getName());
    }

    @Test
    public void testToBooking() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusHours(1));
        dto.setItemId(1L);
        dto.setStatus(StatusEnum.APPROVED);

        Item item = new Item();
        item.setId(dto.getItemId());
        item.setName("вещь");

        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");

        Booking booking = BookingMapper.toBooking(dto, booker, item);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(dto.getStatus(), booking.getStatus());
    }
}

