package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private HashMap<Integer, User> users = new HashMap<>();
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
    public User updateUser(Integer id, User user) {
        User updateUser = users.get(id);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }

        users.put(id, updateUser);
        return updateUser;
    }

    @Override
    public void removeUserById(Integer id) {
        users.remove(id);
    }

    @Override
    public Integer getUserIdByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (User user : users.values()) {
            String email1 = user.getEmail();
            if (email1.equals(email)) {
                return user.getId();
            }
        }
        return null;
    }
}
