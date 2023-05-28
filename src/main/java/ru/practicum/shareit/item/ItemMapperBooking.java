package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

public interface ItemMapperBooking {
    public static ItemDtoForBooking toDto(Item item) {
        ItemDtoForBooking itemDto = new ItemDtoForBooking();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequest(item.getRequest());
        itemDto.setComments(item.getComments().stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList()));

        return itemDto;
    }

    public static Item toItem(ItemDtoForBooking itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());
        item.setComments(itemDto.getComments().stream()
                .map(dto -> CommentMapper.toComment(dto, user))
                .collect(Collectors.toList()));

        return item;
    }
}

