package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Enum.StatusEnum;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusEnum status;
    private Product item;
    private Booker booker;

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public static class Product {
        private final long id;
        private final String name;
    }
}

