package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUserById(Long id);

    List<User> findAllUsers();

    User updateUser(Long id, User user);

    void removeUserById(Long id);

    Long getUserIdByEmail(String email);
}
