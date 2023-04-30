package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Integer, Item> items = new HashMap<>();
    private Integer id = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findItemById(Integer id) {
        return items.get(id);
    }

    @Override
    public Item updateItem(Integer id, Item item) {
        Item updateItem = items.get(id);
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        items.put(id, updateItem);
        return updateItem;
    }

}
