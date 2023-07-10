package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = User.builder()
                .name("user")
                .email("user@yandex.ru")
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserItemsAndBookings() {
        Item item = new Item();
        Booking booking = new Booking();

        User user = User.builder()
                .name("user")
                .email("user@yandex.ru")
                .items(List.of(item))
                .bookings(List.of(booking))
                .comments(new ArrayList<>())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }
}
