package ru.practicum.shareit.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.utils.Utils;

import javax.validation.Valid;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long owner,
            @Valid @RequestBody ItemRequestDto requestDto
    ) {
        return requestClient.createRequest(requestDto, owner);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long id
    ) {
        return requestClient.getAllItemRequest(id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return requestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long requestId
    ) {
        return requestClient.getItemRequestById(requesterId, requestId);
    }
}

