package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

    public Item createItem (Item item);

    public Item findItemById(Integer id);

    public Item updateItem(Integer id, Item item);

}
