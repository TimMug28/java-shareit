package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    void removeUserById(Long id);

    UserDto updateUser(Long id, UserDto userDto);
}
