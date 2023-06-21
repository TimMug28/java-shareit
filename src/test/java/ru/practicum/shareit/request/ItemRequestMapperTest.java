package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    @Test
    public void testToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("описание");

        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("user");
        itemRequest.setRequestor(requestor);

        LocalDateTime created = LocalDateTime.now();
        itemRequest.setCreated(created);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getRequestor().getId(), itemRequestDto.getRequestor().getId());
        assertEquals(itemRequest.getRequestor().getName(), itemRequestDto.getRequestor().getName());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void testToItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("описание");

        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, created);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(created, itemRequest.getCreated());
    }
}
