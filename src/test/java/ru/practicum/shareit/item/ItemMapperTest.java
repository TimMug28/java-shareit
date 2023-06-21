package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    @Test
    public void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("вещь");
        item.setDescription("описание");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1L);
        owner.setName("user");
        item.setOwner(owner);

        Long requestId = 3L;
        item.setRequestId(requestId);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner().getId(), itemDto.getOwner().getId());
        assertEquals(item.getOwner().getName(), itemDto.getOwner().getName());
        assertEquals(item.getRequestId(), itemDto.getRequestId());
    }

    @Test
    public void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("вещь");
        itemDto.setDescription("описание");
        itemDto.setAvailable(true);

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }
}
