package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUserById(Integer id);

    List<User> findAllUsers();

    User updateUser(User user);

    void removeUserById(Integer id);

    Long getUserIdByEmail(String email);
}
