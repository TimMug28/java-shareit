package ru.practicum.shareit.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.item.dto.CommentNewRequestDto;
import ru.practicum.shareit.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.utils.Utils;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemDto
    ) {
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long id,
            @RequestBody ItemRequestDto itemDto

    ) {
        return itemClient.updateItem(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return itemClient.findItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForItemByDescription(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return itemClient.searchForItemByDescription(text, userId, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentNewRequestDto commentNewDto
    ) {
        return itemClient.createComment(commentNewDto, itemId, userId);
    }
}

