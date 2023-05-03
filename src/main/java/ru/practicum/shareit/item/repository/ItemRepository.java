package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository {

    Item createItem(Item item);

    Item findItemById(Long id);

    Item updateItem(Long id, Item item);

    List<Item> getAllItems(Long owner);

    Set<Item> searchForItemByDescription(String text);
}
