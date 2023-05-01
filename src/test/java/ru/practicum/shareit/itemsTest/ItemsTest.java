package ru.practicum.shareit.itemsTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.service.ItemService;

@SpringBootTest
public class ItemsTest {
    ItemService itemService;

    ItemsTest(ItemService itemService) {
        this.itemService = itemService;
    }

    @Test
    void createdItem() {
    }
}
