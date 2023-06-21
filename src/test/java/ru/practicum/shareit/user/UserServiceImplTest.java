package ru.practicum.shareit.user;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void start() {
        userDto = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto added = userService.createUser(userDto);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserExceptionTest() {
        when(userRepository.save(any(User.class)))
                .thenThrow(RuntimeException.class);

        Throwable thrown = Assertions.catchException(() -> userService.createUser(userDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(RuntimeException.class);

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserTest() {
        final UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("updateName")
                .email("updateName@yandex.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto updatedUser = userService.updateUser(1L, updatedUserDto);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updateName@yandex.ru");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updateName");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserExceptionTest() {
        final UserDto updatedUserDto = UserDto.builder()
                .id(99L)
                .name("user99")
                .email("user@yandex.ru")
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        Throwable thrown = Assertions.catchThrowable(() -> userService.updateUser(3L, updatedUserDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(3L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }


    @Test
    void updateUserNameTest() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("updatedName")
                .build();

        User existingUser = User.builder()
                .id(1L)
                .name("oldName")
                .email("user@yandex.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto updatedUser = userService.updateUser(updatedUserDto.getId(), updatedUserDto);

        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("user@yandex.ru");
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updatedName");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto actual = userService.findUserById(1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByIdExceptionTest() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> userService.findUserById(99L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void removeUserTest() {
        Mockito.doNothing().when(userRepository).deleteById(anyLong());

        userService.removeUserById(anyLong());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAllUsersTest() {
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@yandex.ru")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> users = userService.findAllUsers();

        Assertions.assertThat(users)
                .isNotNull()
                .hasSize(2)
                .contains(UserMapper.toUserDto(user),
                        UserMapper.toUserDto(user2));
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}