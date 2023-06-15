package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDto itemDto2;
    private ItemDtoForBooking itemDtoForBooking;

    @BeforeEach
    public void setup() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Лампочка");
        itemDto2.setDescription("Яркая лампочка");
        itemDto2.setAvailable(true);

        itemDtoForBooking = new ItemDtoForBooking();
        itemDtoForBooking.setId(1L);
        itemDtoForBooking.setName("Дрель");
        itemDtoForBooking.setDescription("Описание");
        itemDtoForBooking.setAvailable(true);
        itemDtoForBooking.setLastBooking(new BookingDtoItem());
        itemDtoForBooking.setNextBooking(new BookingDtoItem());
        itemDtoForBooking.setRequest(1L);
    }

    @Test
    void createItemTest() throws Exception {
        Long ownerId = 1L;
        ItemDto createdItemDto = new ItemDto();
        createdItemDto.setId(1L);
        createdItemDto.setName("Дрель");
        createdItemDto.setDescription("Описание");

        when(itemService.createItem(itemDto, ownerId)).thenReturn(createdItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Описание"));

        Mockito.verify(itemService, Mockito.times(1)).createItem(itemDto, ownerId);
    }

    @Test
    void findItemByIdTest() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        when(itemService.findItemById(itemId, ownerId)).thenReturn(itemDtoForBooking);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Описание"))
                .andExpect(jsonPath("$.available").value(true));

        Mockito.verify(itemService, Mockito.times(1)).findItemById(itemId, ownerId);
    }

    @Test
    void getAllItemsTest() throws Exception {
        Long from = 0L;
        Long size = 10L;
        Long ownerId = 1L;
        List<ItemDtoForBooking> itemList = new ArrayList<>();
        itemList.add(itemDtoForBooking);

        when(itemService.getAllItems(ownerId, from, size)).thenReturn(itemList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Описание"))
                .andExpect(jsonPath("$[0].available").value(true));

        Mockito.verify(itemService, Mockito.times(1)).getAllItems(ownerId, from, size);
    }

    @Test
    void updateItemTest() throws Exception {
        Long itemId = 1L;
        Long ownerId = 1L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Отвертка");
        updatedItemDto.setDescription("Новое описание");

        when(itemService.updateItem(itemId, ownerId, itemDto)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Отвертка"))
                .andExpect(jsonPath("$.description").value("Новое описание"));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(itemId, ownerId, itemDto);
    }

    @Test
    void searchForItemByDescriptionTest() throws Exception {
        Long from = 0L;
        Long size = 10L;
        Long ownerId = 1L;

        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(itemDto);
        itemList.add(itemDto2);

        when(itemService.searchForItemByDescription("Дрель", ownerId, from, size))
                .thenReturn(itemList);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("text", "Дрель")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Описание"));

        Mockito.verify(itemService, Mockito.times(1))
                .searchForItemByDescription("Дрель", ownerId, from, size);
    }

    @Test
    void createdCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Комментарий");
        commentDto.setAuthorName("user");

        CommentDto createCommentDto = new CommentDto();
        createCommentDto.setId(1L);
        createCommentDto.setText("Комментарий");
        createCommentDto.setAuthorName("user");
        createCommentDto.setCreatedDate(LocalDateTime.now());

        Long itemId = 1L;
        Long userId = 1L;

        when(itemService.createComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(createCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Комментарий"));
    }
}

