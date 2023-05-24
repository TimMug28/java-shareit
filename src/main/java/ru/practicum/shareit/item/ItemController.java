package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Set;

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
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                              @RequestBody @Validated ItemDto itemDto) {
        log.info("POST /items - создание новой вещи.");
        return itemService.createItem(itemDto, owner);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForBooking findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long owner,
                                          @PathVariable Long itemId) {
        log.info("GET /items - запрос вещи по id.");
        return itemService.findItemById(itemId, owner);
    }

    @GetMapping()
    public List<ItemDtoForBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("GET /items - запрос вещей пользователя " + owner);
        return itemService.getAllItems(owner);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("PATCH /items - создание изменение вещи.");
        return itemService.updateItem(itemId, owner, itemDto);
    }

    @GetMapping("/search")
    public Set<ItemDto> searchForItemByDescription(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                                   @RequestParam(defaultValue = "null") String text
    ) {
        log.info("GET /items/search?text= " + text);
        return itemService.searchForItemByDescription(text, owner);
    }
}
