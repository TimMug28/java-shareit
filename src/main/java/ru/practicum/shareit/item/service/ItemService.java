package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long owner);

    ItemDtoForBooking findItemById(Long id, Long owner);

    ItemDto updateItem(Long id, Long owner, ItemDto itemDto);

    List<ItemDtoForBooking> getAllItems(Long owner);

    List<ItemDto> searchForItemByDescription(String text, Long owner);

    public List<ItemDto> findItemsByUserId(Long ownerId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
