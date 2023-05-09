package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long owner);

    ItemDto findItemById(Long id);

    ItemDto updateItem(Long id, Long owner, ItemDto itemDto);

    List<ItemDto> getAllItems(Long owner);

    Set<ItemDto> searchForItemByDescription(String text, Long owner);
}
