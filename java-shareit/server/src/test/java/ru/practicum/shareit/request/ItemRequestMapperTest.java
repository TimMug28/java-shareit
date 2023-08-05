package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTest
public class ItemRequestMapperTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("описание");
        itemRequestDto.setCreated(now);
        itemRequestDto.setItems(new ArrayList<>());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.created", now)
                .hasJsonPathStringValue("$.description", "описание")
                .hasJsonPathArrayValue("$.items", new ArrayList<>());
    }

    @Test
    void testItemRequestDto_WithItem() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Item item = new Item();
        item.setId(2L);
        item.setName("вещь");
        item.setAvailable(true);
        item.setRequestId(3L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("описание");
        itemRequestDto.setCreated(now);
        itemRequestDto.setItems(List.of(item));


        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.created", now)
                .hasJsonPathStringValue("$.description", "описание")
                .hasJsonPathArrayValue("$.items", List.of(item))
                .hasJsonPathNumberValue("$.items[0].id", 2L)
                .hasJsonPathStringValue("$.items[0].name", "вещь")
                .hasJsonPathBooleanValue("$.items[0].available", true)
                .hasJsonPathNumberValue("$.items[0].requestId", 3L);
    }
}
