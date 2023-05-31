package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Owner owner;
    @JsonIgnore
    private Long request;

    @Data
    public static class Owner {
        private final long id;
        private final String name;
    }
}
