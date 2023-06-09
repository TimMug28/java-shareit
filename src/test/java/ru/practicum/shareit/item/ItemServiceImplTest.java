package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDto itemDto2;
    private ItemDtoForBooking itemDtoForBooking;
    private Item item2;

    @BeforeEach
    void start() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        itemDto = new ItemDto();
        itemDto.setName("лампочка");
        itemDto.setDescription("яркая лампочка");
        itemDto.setAvailable(true);

        item = Item.builder()
                .id(1L)
                .name("лампочка")
                .description("яркая лампочка")
                .available(true)
                .owner(user)
                .build();

        itemDto2 = new ItemDto();
        itemDto2.setId(1L);
        itemDto2.setName("лампочка");
        itemDto2.setDescription("яркая лампочка");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(null);

        itemDtoForBooking = new ItemDtoForBooking();
        itemDtoForBooking.setId(1L);
        itemDtoForBooking.setName("лампочка");
        itemDtoForBooking.setDescription("яркая лампочка");
        itemDtoForBooking.setAvailable(true);
        itemDtoForBooking.setComments(new ArrayList<>());
        itemDtoForBooking.setNextBooking(null);
        itemDtoForBooking.setLastBooking(null);

        item2 = Item.builder()
                .id(2L)
                .name("утюг")
                .description("просто утюг")
                .available(false)
                .owner(user)
                .build();
    }


    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createItem = itemService.createItem(itemDto, 1L);


        Assertions.assertThat(createItem)
                .isNotNull()
                .usingRecursiveComparison();

        Assertions.assertThat(createItem.getRequestId()).isNull();

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository);
    }


}
