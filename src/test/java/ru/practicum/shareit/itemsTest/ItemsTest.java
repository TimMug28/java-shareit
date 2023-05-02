package ru.practicum.shareit.itemsTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemsTest {
    ItemService itemService;
    UserRepositoryImpl userService;
    Item item;
    Item item1;
    User user;

    @BeforeEach
    void start() {
        userService = new UserRepositoryImpl();
        itemService = new ItemService(new ItemRepositoryImpl(), userService);
        item = new Item();
        item1 = new Item();
        user = new User();
        user.setName("Bob");
        user.setEmail("boby@mail.ru");
        userService.createUser(user);
        item.setName("Дрель");
        item.setDescription("супер дрель");
        item.setAvailable(true);
        item.setOwner(1);
        item1.setName("Отвертка");
        item1.setDescription("супер отвертка");
        item1.setAvailable(true);
        item1.setOwner(1);
    }

    @Test
    void testCreatedItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemService.createItem(itemDto, 1);
        List<ItemDto> itemDtoList = itemService.getAllItems(1);
        assertEquals(1, itemDtoList.size());

    }

    @Test
    void testUpdateItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto createdItem = itemService.createItem(itemDto, 1);
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto savedItem = itemService.updateItem(createdItem.getId(), 1, itemDto1);
        assertEquals(savedItem.getId(), createdItem.getId());
        assertEquals(savedItem.getName(), item1.getName());
        assertEquals(savedItem.getDescription(), item1.getDescription());
        assertEquals(savedItem.getAvailable(), item1.getAvailable());
    }

    @Test
    void testFindItemById() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto createdItem = itemService.createItem(itemDto, 1);
        ItemDto foundItem = itemService.findItemById(createdItem.getId());
        assertNotNull(foundItem);
        assertEquals(createdItem.getId(), foundItem.getId());
        assertEquals(createdItem.getName(), foundItem.getName());
        assertEquals(createdItem.getDescription(), foundItem.getDescription());
        assertEquals(createdItem.getAvailable(), foundItem.getAvailable());
    }

    @Test
    void testGetAllItems() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto createdItem = itemService.createItem(itemDto, 1);
        List<ItemDto> itemDtoList = itemService.getAllItems(1);
        assertEquals(1, itemDtoList.size());
    }

    @Test
    void testSearchForItemByDescription() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto createdItem = itemService.createItem(itemDto, 1);
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto createdItem1 = itemService.createItem(itemDto1, 1);
        Set<ItemDto> itemDtoSet = itemService.searchForItemByDescription("дрель", 1);
        ItemDto foundItem = itemDtoSet.iterator().next();
        assertNotNull(itemDtoSet);
        assertEquals(createdItem.getId(), foundItem.getId());
        assertEquals(createdItem.getName(), foundItem.getName());
        assertEquals(createdItem.getDescription(), foundItem.getDescription());
        assertEquals(createdItem.getAvailable(), foundItem.getAvailable());
    }
}
