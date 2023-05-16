package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long owner);

    List<BookingDto> findAllBookings(Long bookingId, Long userId);

    BookingDto updateBookingStatus(Long id , boolean approved, Long ownerId);
    List<BookingDto> findBookingUsers(String state, Long userid);

    List<BookingDto> getOwnerBookings(Long userId, String state);
}