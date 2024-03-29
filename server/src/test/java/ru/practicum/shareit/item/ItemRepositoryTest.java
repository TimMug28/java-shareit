package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    User user;
    Item item;

    @BeforeEach
    void start() {
        user = User.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        userRepository.save(user);
        itemRepository.save(Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(null)
                .owner(user)
                .build());

        item = Item.builder()
                .name("Лампа")
                .description("просто лампа")
                .available(true)
                .requestId(null)
                .owner(user)
                .build();
        itemRepository.save(item);
    }

    @Test
    void testFindAllByOwnerOrderById() {
        int page = 0 / 5;
        PageRequest pageRequest = PageRequest.of(page, 5);
        List<Item> itemList = itemRepository.findAllByOwnerOrderById(user, pageRequest);

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        int page = 0 / 5;
        PageRequest pageRequest = PageRequest.of(page, 5);
        List<Item> itemList = itemRepository.searchItemsByDescription("просто", pageRequest);
        assertNotNull(itemList);
        assertEquals(1, itemList.size());
    }
}