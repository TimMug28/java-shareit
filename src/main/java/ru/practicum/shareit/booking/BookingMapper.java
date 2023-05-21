package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem().getId());
        dto.setStatus(booking.getStatus());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        return dto;
    }

    public static Booking toBooking(BookingDto dto, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(dto.getStatus());
        return booking;
    }

//    public static BookingDtoItem toDtoItem(Booking booking, Long booker) {
//        BookingDtoItem dto = new BookingDtoItem();
//        dto.setId(booking.getId());
//        dto.setStart(booking.getStart());
//        dto.setEnd(booking.getEnd());
//        dto.setBookerId(booker);
//        dto.setStatus(booking.getStatus());
//        return dto;
//    }
//
//    public static Booking toBookingItem(BookingDtoItem dto, Long booker) {
//        Booking booking = new Booking();
//        booking.setId(dto.getId());
//        booking.setStart(dto.getStart());
//        booking.setEnd(dto.getEnd());
//        Item item = new Item();
//        booking.setItem(item);
//        User user = new User();
//        booking.setBooker(booker);
//        booking.setStatus(dto.getStatus());
//        return booking;
//    }
}

