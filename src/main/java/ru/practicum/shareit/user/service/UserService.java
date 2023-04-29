package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    UserRepository userRepository;

    public UserDto createUser(UserDto user) {
        User user1 = UserMapper.toUser(user);
        UserDto createdUser = UserMapper.toUserDto(userRepository.createUser(user1));
        log.info("Добавлен новый пользователь: {}", createdUser);
        return createdUser;
    }

    public List<UserDto> findAllUsers() {
        log.info("Запрос списка всех пользователей");
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.findAllUsers();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }

    public UserDto findUserById(Integer id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        User user = userRepository.getUserById(id);
        if (user == null) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
            return null;
        }
        log.info("Запрошен пользователь c id={}.", id);
        return UserMapper.toUserDto(user);
    }

    public void removeUserById(Integer id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        User user = userRepository.getUserById(id);
        if (user == null) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
        }
        userRepository.removeUserById(id);
    }

    public UserDto updateUser(Integer id) {
        UserDto userDto = findUserById(id);
        User user = userRepository.updateUser(UserMapper.toUser(userDto));
        return null;
    }
}
