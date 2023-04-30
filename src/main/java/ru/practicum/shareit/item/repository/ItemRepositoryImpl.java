package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap <Integer, Item> items = new HashMap<>();
    private Integer id = 1;
    @Override
    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

}
