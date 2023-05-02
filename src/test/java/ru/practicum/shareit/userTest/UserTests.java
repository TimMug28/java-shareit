package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTests {
    UserService userService;
    User user;
    User user2;


    @BeforeEach
    void start() {
        userService = new UserService(new UserRepositoryImpl());
        user = new User();
        user2 = new User();
        user.setName("Bob");
        user.setEmail("boby@mail.ru");
        user2.setName("Sam");
        user2.setEmail("samy@mail.ru");
    }

    @Test
    void createUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        userService.createUser(userDto);
        List<UserDto> userDtoList = userService.findAllUsers();
        assertEquals(1, userDtoList.size());
    }

    @Test
    void findAllUsers() {
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto2 = UserMapper.toUserDto(user2);
        userService.createUser(userDto);
        userService.createUser(userDto2);
        List<UserDto> userDtoList = userService.findAllUsers();
        assertEquals(2, userDtoList.size());
    }

    @Test
    void updateUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto2 = UserMapper.toUserDto(user2);
        userService.createUser(userDto);
        userService.updateUser(1, userDto2);
        UserDto updateUser = userService.findUserById(1);
        assertEquals("Sam", updateUser.getName());
    }

    @Test
    void removeUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        userService.createUser(userDto);
        List<UserDto> userDtoList = userService.findAllUsers();
        assertEquals(1, userDtoList.size());
        userService.removeUserById(1);
        List<UserDto> userDtoList1 = userService.findAllUsers();
        assertEquals(0, userDtoList1.size());
    }
}
