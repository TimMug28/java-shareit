package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long id);

    List<ItemRequestDto> getAllItemRequest(Long id);

    List<ItemRequestDto> getAllRequestOtherUsers(Long requesterId, int from, int size);

    ItemRequestDto getItemRequestById(Long requesterId, Long requestId);
}
