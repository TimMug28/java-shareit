package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.booking.dto.BookingNewRequestDto;
import ru.practicum.shareit.shareit.exceptions.ValidationException;

import javax.validation.Valid;

import static ru.practicum.shareit.shareit.utils.Utils.checkPaging;


@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingNewRequestDto bookingNewDto
    ) {
        if (bookingNewDto.getStart().isEqual(bookingNewDto.getEnd())) {
            throw new ValidationException("Время начала и окончания бронирования совпадают.");
        }

        if (bookingNewDto.getStart().isAfter(bookingNewDto.getEnd())) {
            throw new ValidationException("Время начала бронирования не может быть в прошлом.");
        }
        return bookingClient.createBooking(bookingNewDto, userId);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingClient.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingDetails(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        return bookingClient.getBookingDetails(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingUsers(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        checkPaging(from, size);
        try {
            StateEnum status = StateEnum.valueOf(state);
            return bookingClient.findBookingUsers(state, userId, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        checkPaging(from, size);
        try {
            StateEnum status = StateEnum.valueOf(state);
            return bookingClient.getOwnerBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}

