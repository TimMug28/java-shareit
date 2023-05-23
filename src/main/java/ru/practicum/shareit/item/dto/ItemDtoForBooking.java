package ru.practicum.shareit.item.dto;


import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoForBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private Long request;
}
