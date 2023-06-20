package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void createRequestTest() throws Exception {
        Long ownerId = 1L;
        ItemRequestDto.Requestor requestor = new ItemRequestDto.Requestor(1L, "user");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("описание");
        itemRequestDto.setRequestor(requestor);

        ItemRequestDto createdItemRequestDto = new ItemRequestDto();
        createdItemRequestDto.setId(1L);
        createdItemRequestDto.setDescription("описание");
        createdItemRequestDto.setRequestor(requestor);
        createdItemRequestDto.setCreated(LocalDateTime.of(2023,10,5,16,23));

        when(itemRequestService.createRequest(any(ItemRequestDto.class),anyLong()))
                .thenReturn(createdItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("описание"));
    }

    @Test
    void getAllItemRequestTest() throws Exception {
        Long userId = 1L;

        ItemRequestDto itemRequest1 = new ItemRequestDto();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Запрос 1");

        ItemRequestDto itemRequest2 = new ItemRequestDto();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Запрос 2");

        List<ItemRequestDto> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        when(itemRequestService.getAllItemRequest(userId)).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Запрос 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Запрос 2"));

        verify(itemRequestService, times(1)).getAllItemRequest(userId);
    }

    @Test
    void getAllRequestsTest() throws Exception {
        Long requesterId = 1L;
        Long from = 0L;
        Long size = 10L;

        ItemRequestDto itemRequest1 = new ItemRequestDto();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Запрос 1");

        ItemRequestDto itemRequest2 = new ItemRequestDto();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Запрос 2");

        List<ItemRequestDto> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        when(itemRequestService.getAllRequestOtherUsers(requesterId, from, size)).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Запрос 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Запрос 2"));

        verify(itemRequestService, times(1)).getAllRequestOtherUsers(requesterId, from, size);
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        Long requesterId = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Запрос");

        when(itemRequestService.getItemRequestById(requesterId, requestId)).thenReturn(itemRequest);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Запрос"));

        verify(itemRequestService, times(1)).getItemRequestById(requesterId, requestId);
    }
}


