package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    User user;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;

    @BeforeEach
    void start() {
        user = User.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        userRepository.save(user);

        itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequestor(user);
        itemRequestRepository.save(itemRequest1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Request 2");
        itemRequest2.setRequestor(user);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void testFindAllByRequestorOrderByIdDesc() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorOrderByIdDesc(user);

        assertNotNull(itemRequestList);
        assertEquals(2, itemRequestList.size());
        assertEquals(itemRequest2.getId(), itemRequestList.get(0).getId());
        assertEquals(itemRequest1.getId(), itemRequestList.get(1).getId());
    }

    @Test
    void testFindAllByRequestorNotOrderByIdDesc() {
        int page = 0 / 5;
        PageRequest pageRequest = PageRequest.of(page, 5);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorNotOrderByIdDesc(user, pageRequest);

        assertNotNull(itemRequestList);
        assertEquals(0, itemRequestList.size());
    }
}

