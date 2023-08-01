package ru.practicum.shareit.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.request.dto.ItemRequestNewDto;
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
            @RequestHeader("X-Sharer-User-Id") long owner,
            @Valid @RequestBody ItemRequestNewDto requestDto
    ) {
        return requestClient.createRequest(requestDto, owner);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequest(
            @RequestHeader("X-Sharer-User-Id") long id
    ) {
        return requestClient.getAllItemRequest(id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return requestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @PathVariable long requestId
    ) {
        return requestClient.getItemRequestById(requesterId, requestId);
    }
}

