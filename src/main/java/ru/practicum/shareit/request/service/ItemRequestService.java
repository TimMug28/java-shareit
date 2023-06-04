package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest (ItemRequestDto itemRequestDto, Long id);

    List<ItemRequestDto> getAllItemRequest (Long id);
}
