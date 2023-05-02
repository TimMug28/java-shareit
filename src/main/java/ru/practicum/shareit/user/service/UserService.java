package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserDto user) {
        validateEmail(UserMapper.toUser(user));
        User user1 = UserMapper.toUser(user);
        UserDto createdUser = UserMapper.toUserDto(userRepository.createUser(user1));
        log.info("Добавлен новый пользователь: {}", createdUser);
        return createdUser;
    }

    public List<UserDto> findAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.findAllUsers();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        log.info("Запрос списка всех пользователей");
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
        log.info("Удалён пользователь c id={}.", id);
    }

    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        validateId(user);
        validateEmail(user);
        user = userRepository.updateUser(id, user);
        log.info("Изменен пользователь c id={}.", id);
        return UserMapper.toUserDto(user);
    }

    private void validateEmail(User user) {
        Integer id = userRepository.getUserIdByEmail(user.getEmail());
        if (id != null && !user.getId().equals(id)) {
            log.info("Ошибка, данный Email уже занят.");
            throw new ConflictException("Повторное использование Email.");
        }
    }

    private void validateId(User user) {
        Integer id = user.getId();
        if (id == null) {
            log.info("id пользователя " + user + " равен null.");
            throw new NotFoundException("id пользователя равен null.");
        }
    }
}
