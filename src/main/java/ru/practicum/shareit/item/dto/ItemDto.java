package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    @JsonIgnore
    private Boolean isRequest;
}
