package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

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
    public List<ItemDto> searchForItemByDescription(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                                    @RequestParam(defaultValue = "null") String text
    ) {
        log.info("GET /items/search?text= " + text);
        return itemService.searchForItemByDescription(text, owner);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Validated CommentDto commentDto) {
        log.info("POST /comment - добавление комментария.");
        return itemService.createComment(commentDto, itemId, userId);
    }

}
