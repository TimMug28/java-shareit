package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long owner);

    List<BookingDto> findAllBookings(Long bookingId, Long userId);

    BookingDto updateBookingStatus(Long id , boolean approved, Long ownerId);
    BookingDto findBookingById(Long id);

    List<BookingDto> findAllBookingUser();
}
