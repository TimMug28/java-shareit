package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer owner,
                              @RequestBody @Validated ItemDto itemDto) {
        log.info("POST /items - создание новой вещи.");
        return itemService.createItem(itemDto, owner);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Integer itemId) {
        log.info("GET /items - запрос вещи по id.");
        return itemService.findItemById(itemId);
    }

//    @GetMapping()
//    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
//        return itemService.getAllItems(userId);
//    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer owner,
                              @PathVariable  Integer itemId,
                              @RequestBody ItemDto itemDto)  {
        log.info("PATCH /items - создание изменение вещи.");
        return itemService.updateItem(itemId, owner, itemDto);
    }
}
