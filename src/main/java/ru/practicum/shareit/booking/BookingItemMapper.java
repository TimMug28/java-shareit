package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingItemMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "booker.id", source = "bookerId")
    Booking toItem(BookingDtoItem bookingForItemDto);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoItem toDto(Booking booking);
}


