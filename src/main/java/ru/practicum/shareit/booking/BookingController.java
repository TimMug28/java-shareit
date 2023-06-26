package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                    @RequestBody @Validated BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, owner);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") boolean approved,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        return bookingService.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingDetails(@PathVariable("bookingId") Long bookingId,
                                        @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingDetails(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingUsers(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @RequestParam(name = "size", defaultValue = "20") Long size) {
        try {
            StateEnum status = StateEnum.valueOf(state);
            return bookingService.findBookingUsers(status, userId, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @RequestParam(name = "size", defaultValue = "20") Long size) {
        try {
            StateEnum status = StateEnum.valueOf(state);
            return bookingService.getOwnerBookings(userId, status, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
