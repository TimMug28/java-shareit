package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class ItemRequestDto {
    private Long id;
    @NotNull
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
