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
        return itemService.createItem(itemDto, owner);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForBooking findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long owner,
                                          @PathVariable Long itemId) {
        return itemService.findItemById(itemId, owner);
    }

    @GetMapping()
    public List<ItemDtoForBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") Long owner,
                                               @RequestParam(name = "from", defaultValue = "0") Long from,
                                               @RequestParam(name = "size", defaultValue = "20") Long size) {
        return itemService.getAllItems(owner, from, size);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, owner, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItemByDescription(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long owner,
                                                    @RequestParam(defaultValue = "null") String text,
                                                    @RequestParam(name = "from", defaultValue = "0") Long from,
                                                    @RequestParam(name = "size", defaultValue = "20") Long size
    ) {
        return itemService.searchForItemByDescription(text, owner, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Validated CommentDto commentDto) {
        return itemService.createComment(commentDto, itemId, userId);
    }

}
