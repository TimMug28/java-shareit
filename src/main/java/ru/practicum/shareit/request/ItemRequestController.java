package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                        @RequestBody @Validated ItemRequestDto itemRequestDto) {
        log.info("POST /requests - добавление запроса.");
        return itemRequestService.createRequest(itemRequestDto, owner);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET /requests - получение списка запросов пользователя: " + id);
        return itemRequestService.getAllItemRequest(id);
    }
}
