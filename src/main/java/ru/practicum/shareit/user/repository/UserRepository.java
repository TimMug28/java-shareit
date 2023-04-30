package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUserById(Integer id);

    List<User> findAllUsers();

    User updateUser(Integer id, User user);

    void removeUserById(Integer id);

    Integer getUserIdByEmail(String email);
}
