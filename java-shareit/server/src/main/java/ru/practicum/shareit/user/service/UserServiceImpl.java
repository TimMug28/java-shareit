package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto user) {
        User createdUser = UserMapper.toUser(user);
        UserDto userDto = UserMapper.toUserDto(userRepository.save(createdUser));
        log.info("Добавлен новый пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Запрос списка всех пользователей");
        return users
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        Optional<User> getUser = userRepository.findById(id);
        if (getUser.isEmpty()) {
            log.info("Не найден пользователь c id={}.", id);
            throw new NotFoundException("Пользователь не найден.");
        }
        return UserMapper.toUserDto(getUser.get());
    }

    @Override
    public void removeUserById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        userRepository.deleteById(id);
        log.info("Удалён пользователь c id={}.", id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            log.info("Изменен пользователь c id={}.", id);
            return UserMapper.toUserDto(userRepository.save(user));
        } else {
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}