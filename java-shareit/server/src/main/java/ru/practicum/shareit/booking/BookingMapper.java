package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem().getId());
        dto.setStatus(booking.getStatus());
        Long itemId = booking.getItem().getId();
        String itemName = booking.getItem().getName();
        BookingDto.Product product = new BookingDto.Product(itemId,itemName);
        dto.setItem(product);
        Long bookerId = booking.getBooker().getId();
        String bookerName = booking.getBooker().getName();
        BookingDto.Booker booker = new BookingDto.Booker(bookerId, bookerName);
        dto.setBooker(booker);
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
}

