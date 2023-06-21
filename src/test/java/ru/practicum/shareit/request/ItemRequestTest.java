package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemRequestTest {

    private final Validator validator;

    public ItemRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidItemRequest() {
        User user = new User();
        LocalDateTime created = LocalDateTime.now();
        List<Item> items = new ArrayList<>();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("запрос");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(created);
        itemRequest.setItems(items);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(itemRequest);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void testItemRequestWithItems() {
        User user = new User();
        LocalDateTime created = LocalDateTime.now();
        List<Item> items = new ArrayList<>();
        items.add(new Item());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("запрос");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(created);
        itemRequest.setItems(items);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(itemRequest);

        Assertions.assertTrue(violations.isEmpty());
    }
}

