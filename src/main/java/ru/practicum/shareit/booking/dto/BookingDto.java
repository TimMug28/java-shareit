package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.StatusEnum;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusEnum status;
}
