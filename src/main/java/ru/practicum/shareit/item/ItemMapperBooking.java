package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

public interface ItemMapperBooking {
    static ItemDtoForBooking toDto(Item item) {
        ItemDtoForBooking itemDto = new ItemDtoForBooking();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(item.getRequestId());
        itemDto.setComments(item.getComments().stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList()));

        return itemDto;
    }
}

