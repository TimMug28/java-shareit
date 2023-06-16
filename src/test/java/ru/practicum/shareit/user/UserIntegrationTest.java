package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserIntegrationTest {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void start() {
        user1 = User.builder()
                .name("user1")
                .email("user1@yandex.ru")
                .build();
        entityManager.persist(user1);
        user2 = User.builder()
                .name("user2")
                .email("user2@yandex.ru")
                .build();
        entityManager.persist(user2);
    }

    @Test
    void saveUserTest() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("user@yandex.ru").build();

        UserDto saved = userService.createUser(userDto);

        Assertions.assertThat(saved).isNotNull()
                .hasFieldOrPropertyWithValue("name", "user")
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru")
                .hasFieldOrProperty("id")
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void updateUserTest() {
        UserDto userDto = UserMapper.toUserDto(user1);

        userDto.setName("userUpdated");
        userDto.setEmail("userUpdated@yandex.ru");

        UserDto updated = userService.updateUser(user1.getId(), userDto);

        entityManager.merge(user1);

        Assertions.assertThat(updated).isNotNull()
                .hasFieldOrPropertyWithValue("name", "userUpdated")
                .hasFieldOrPropertyWithValue("email", "userUpdated@yandex.ru")
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void findUserByIdTest() {
        Long userId = user1.getId();

        UserDto returned = userService.findUserById(userId);

        Assertions.assertThat(returned).isNotNull()
                .hasFieldOrPropertyWithValue("name", "user1")
                .hasFieldOrPropertyWithValue("email", "user1@yandex.ru")
                .hasFieldOrPropertyWithValue("id", userId)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void deleteUserTest() {
        Long userId = user1.getId();

        long beforeDelete = userRepository.count();
        Assertions.assertThat(beforeDelete).isEqualTo(2);

        userService.removeUserById(userId);

        long afterDelete = userRepository.count();
        Assertions.assertThat(afterDelete).isEqualTo(1);

        Optional<User> user = userRepository.findById(userId);
        Assertions.assertThat(user)
                .isNotPresent();
    }

    @Test
    void findAllUsersTest() {
        List<UserDto> list = userService.findAllUsers();

        Assertions.assertThat(list)
                .isNotEmpty()
                .hasSize(2)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(user1, user2));
    }
}
