package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapperBooking {

    Item toItem(ItemDtoForBooking itemWithBookingDto);

    ItemDtoForBooking toDto(Item item);
}
