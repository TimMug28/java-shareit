package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
public class UserTests {
    UserService userService;

    UserTests(UserService userService) {
        this.userService = userService;
    }

    @Test
    void createUser() {
    }
}
