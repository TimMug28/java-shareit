//package ru.practicum.shareit.user.repository;
//
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@Repository
//public class UserRepositoryImpl implements UserRepository {
//    private final HashMap<Long, User> users = new HashMap<>();
//    private Long id = 1L;
//
//
//    @Override
//    public User createUser(User user) {
//        user.setId(id++);
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public User getUserById(Long id) {
//        return users.get(id);
//    }
//
//    @Override
//    public List<User> findAllUsers() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public User updateUser(Long id, User user) {
//        User updateUser = users.get(id);
//
//        if (user.getName() != null) {
//            updateUser.setName(user.getName());
//        }
//        if (user.getEmail() != null) {
//            updateUser.setEmail(user.getEmail());
//        }
//        users.put(id, updateUser);
//        return updateUser;
//    }
//
//    @Override
//    public void removeUserById(Long id) {
//        users.remove(id);
//    }
//
//    @Override
//    public Long getUserIdByEmail(String email) {
//        if (email == null) {
//            return null;
//        }
//        for (User user : users.values()) {
//            String userEmail = user.getEmail();
//            if (userEmail.equals(email)) {
//                return user.getId();
//            }
//        }
//        return null;
//    }
//}
