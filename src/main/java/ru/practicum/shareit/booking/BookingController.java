package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                    @RequestBody @Validated BookingDto bookingDto) {
        log.info("POST /bookings - создание бронирования.");
        return bookingService.createBooking(bookingDto, owner);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") boolean approved,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("PATCH /bookings/{} - обновление статуса бронирования.", bookingId);
        return bookingService.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public List<BookingDto> getBookingDetails(@PathVariable("bookingId") Long bookingId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} - получение данных о бронировании.", bookingId);
        return bookingService.findAllBookings(bookingId, userId);
    }
}
