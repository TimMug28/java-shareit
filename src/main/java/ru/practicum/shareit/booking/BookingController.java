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
    public BookingDto getBookingDetails(@PathVariable("bookingId") Long bookingId,
                                        @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} - получение данных о бронировании.", bookingId);
        return bookingService.getBookingDetails(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingUsers(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings - получение списка бронирований пользователя.");
        return bookingService.findBookingUsers(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/owner - получение списка бронирований для всех вещей текущего пользователя.");
        return bookingService.getOwnerBookings(userId, state);
    }
}
