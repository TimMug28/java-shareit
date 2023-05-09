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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto user) {
        validateEmail(UserMapper.toUser(user));
        User createdUser = UserMapper.toUser(user);
        UserDto userDto = UserMapper.toUserDto(userRepository.createUser(createdUser));
        log.info("Добавлен новый пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        log.info("Запрос списка всех пользователей");
        return users
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        User user = userRepository.getUserById(id);
        if (user == null) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
            return null;
        }
        log.info("Запрошен пользователь c id={}.", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUserById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        User user = userRepository.getUserById(id);
        if (user == null) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
        }
        userRepository.removeUserById(id);
        log.info("Удалён пользователь c id={}.", id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        validateId(user);
        validateEmail(user);
        user = userRepository.updateUser(id, user);
        log.info("Изменен пользователь c id={}.", id);
        return UserMapper.toUserDto(user);
    }

    private void validateEmail(User user) {
        Long id = userRepository.getUserIdByEmail(user.getEmail());
        if (id != null && !user.getId().equals(id)) {
            log.info("Ошибка, данный Email уже занят.");
            throw new ConflictException("Повторное использование Email.");
        }
    }

    private void validateId(User user) {
        Long id = user.getId();
        if (id == null) {
            log.info("id пользователя " + user + " равен null.");
            throw new NotFoundException("id пользователя равен null.");
        }
    }
}
