package ru.practicum.shareit.booking;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepoitory;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User user2;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void start() {
        user = User.builder()
                .id(1L).name("user").email("user@example.com")
                .build();

        user = User.builder()
                .id(1L).name("user2").email("user2@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("молоток")
                .description("стальной молоток")
                .owner(user2)
                .available(true)
                .build();


        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .booker(user)
                .item(item)
                .status(StatusEnum.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .booker(new BookingDto.Booker(user.getId(),user.getName()))
                .item(new BookingDto.Product(item.getId(), item.getName()))
                .status(StatusEnum.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

    }

    @Test
    void createBookingExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(bookingDto,99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, times(1)).findById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}

