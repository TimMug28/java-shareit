package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - создание пользователя");
        return userService.createUser(userDto);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.info("GET /user - все пользователи");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(
            @PathVariable Integer id) {
        log.info("GET /users/{} - пользователь", id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeUser(@PathVariable Integer id) {
        log.info("DELETE /users/{} - запрос на удаление пользователя.", id);
        userService.removeUserById(id);
        return ResponseEntity.ok("Пользователь удален.");
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@PathVariable Integer userId,
                       @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - запрос на изменение пользователя.", userId);
        return userService.updateUser(userId, userDto);
    }
}
