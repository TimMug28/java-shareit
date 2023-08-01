package ru.practicum.shareit.booking;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-31T22:22:27+0500",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.19 (Amazon.com Inc.)"
)
@Component
public class BookingItemMapperImpl implements BookingItemMapper {

    @Override
    public BookingDtoItem toDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDtoItem bookingDtoItem = new BookingDtoItem();

        bookingDtoItem.setStatus( booking.getStatus() );
        bookingDtoItem.setBookerId( bookingBookerId( booking ) );
        bookingDtoItem.setId( booking.getId() );
        bookingDtoItem.setStart( booking.getStart() );
        bookingDtoItem.setEnd( booking.getEnd() );

        return bookingDtoItem;
    }

    private Long bookingBookerId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        User booker = booking.getBooker();
        if ( booker == null ) {
            return null;
        }
        Long id = booker.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
