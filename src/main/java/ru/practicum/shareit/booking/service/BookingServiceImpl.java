package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public class BookingServiceImpl implements BookingService{
    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long owner) {
        return null;
    }

    @Override
    public List<BookingDto> findAllBookings(Long bookingId, Long userId) {
        return null;
    }

    @Override
    public BookingDto updateBookingStatus(Long id , boolean approved, Long ownerId) {
        return null;
    }

    @Override
    public BookingDto findBookingById(Long id) {
        return null;
    }

    @Override
    public List<BookingDto> findAllBookingUser() {
        return null;
    }
}
