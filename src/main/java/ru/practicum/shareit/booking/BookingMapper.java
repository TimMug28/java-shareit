package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(booking.getItem().getName());
        dto.setBooker(booking.getBooker().getName());
        dto.setStatus(booking.getStatus().toString());
        return dto;
    }

    public Booking toBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        // Создание пустых объектов Item и User для установки связей
        Item item = new Item();
        item.setName(dto.getItem());
        booking.setItem(item);
        User booker = new User();
        booker.setName(dto.getBooker());
        booking.setBooker(booker);
        booking.setStatus(StatusEnum.valueOf(dto.getStatus()));
        return booking;
    }
}

