package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository {

    Item createItem(Item item);

    Item findItemById(Integer id);

    Item updateItem(Integer id, Item item);

    List<Item> getAllItems(Integer owner);

    Set<Item> searchForItemByDescription(String text);
}
