package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Requestor requestor;
    private LocalDateTime created;
    private List<Item> items;

    @Data
    public static class Requestor {
        private final long id;
        private final String name;
    }
}
