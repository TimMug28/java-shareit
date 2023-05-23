package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;

public class ItemMapperBooking {
    public static ItemDtoForBooking toItemDtoForBooking(Item item) {
        ItemDtoForBooking dto = new ItemDtoForBooking();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
//        dto.setLastBooking(BookingMapper.toBooking(item.getLastBooking()));
//        dto.setNextBooking(BookingMapper.toBooking(item.getNextBooking()));
        dto.setRequest(item.getRequest());
        return dto;
    }
}
