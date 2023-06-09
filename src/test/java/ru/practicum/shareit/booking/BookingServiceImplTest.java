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
    private BookingRepository bookingRepository;

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

        user2 = User.builder()
                .id(2L).name("user2").email("user2@example.com")
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
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .booker(user)
                .item(item)
                .status(StatusEnum.WAITING)
                .build();

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setBooker(new BookingDto.Booker(user.getId(), user.getName()));
        bookingDto.setItem(new BookingDto.Product(item.getId(), item.getName()));
        bookingDto.setStatus(StatusEnum.WAITING);
        bookingDto.setStart(now.plusHours(1));
        bookingDto.setEnd(now.plusHours(2));
    }

    @Test
    void createBookingTest() {
        bookingDto.setItemId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto createdBookingDto = bookingService.createBooking(bookingDto, 1L);
        Assertions.assertThat(createdBookingDto).isNotNull();
        Assertions.assertThat(createdBookingDto.getItemId()).isEqualTo(1L);
        Assertions.assertThat(createdBookingDto.getStatus()).isEqualTo(StatusEnum.WAITING);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void createBookingExceptionTest() {
        when(userRepository.findById(eq(99L)))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> bookingService.createBooking(bookingDto, 99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, times(1)).findById(eq(99L));
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}

