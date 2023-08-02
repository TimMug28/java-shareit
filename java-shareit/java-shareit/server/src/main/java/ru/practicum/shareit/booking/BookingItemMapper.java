package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;

@Component
@Mapper(componentModel = "spring")
public class BookingItemMapper {

    @Mapping(target = "bookerId", source = "booker.id")
    public BookingDtoItem toDto(Booking booking) {
        BookingDtoItem bookingDtoItem = new BookingDtoItem();
        bookingDtoItem.setId(booking.getId());
        bookingDtoItem.setStart(booking.getStart());
        bookingDtoItem.setEnd(booking.getEnd());
        bookingDtoItem.setBookerId(booking.getBooker().getId());
        bookingDtoItem.setStatus(booking.getStatus());
        return bookingDtoItem;
    }
}


