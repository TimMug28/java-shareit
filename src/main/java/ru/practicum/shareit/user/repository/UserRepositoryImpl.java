package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private HashMap <Integer, User> users = new HashMap<>();
    private Integer id = 1;


    @Override
    public User createUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {

        final Integer inputId = user.getId();
        final String inputName = user.getName();
        final String inputEmail = user.getEmail();

        User updateUser = users.get(inputId);

        users.put(inputId, updateUser);
        return updateUser;
    }

    @Override
    public void removeUserById(Integer id) {
         users.remove(id);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return null;
    }
}
