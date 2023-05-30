package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long owner);

    BookingDto getBookingDetails(Long bookingId, Long userId);

    BookingDto updateBookingStatus(Long id, boolean approved, Long ownerId);

    List<BookingDto> findBookingUsers(StateEnum state, Long userid);

    List<BookingDto> getOwnerBookings(Long userId, StateEnum state);
}
