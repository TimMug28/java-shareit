package ru.practicum.shareit.booking;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
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
    private Booking booking2;

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

        booking2 = Booking.builder()
                .id(2L)
                .start(now.plusHours(2))
                .end(now.plusHours(4))
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

    @Test
    void updateBookingStatusTest() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        boolean approved = true;

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking updatedBooking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(StatusEnum.APPROVED)
                .build();

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(updatedBooking);

        BookingDto result = bookingService.updateBookingStatus(bookingId, approved, ownerId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getStatus()).isEqualTo(StatusEnum.APPROVED);

        Mockito.verify(userRepository, Mockito.times(1)).findById(ownerId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }


    @Test
    void updateBookingStatusNotFoundExceptionTest() {
        Long bookingId = 1L;
        Long ownerId = 99L;
        boolean approved = true;

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(ownerId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void updateBookingStatusBookingNotFoundTest() {
        Long bookingId = 99L;
        Long ownerId = 1L;
        boolean approved = true;

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с 99 не найдена.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(ownerId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void updateBookingStatusInvalidOwnerTest() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        boolean approved = true;

        Mockito.when(userRepository.findById(ownerId)).thenReturn(Optional.of(User.builder().id(ownerId).build()));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        Assertions.assertThatThrownBy(() -> bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Попытка редактирования статуса другим пользователем.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(ownerId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getBookingDetailsTest() {
        Long bookingId = 1L;
        Long userId = 1L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BookingDto result = bookingService.getBookingDetails(bookingId, userId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(bookingId);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingDetailsInvalidUserTest() {
        Long bookingId = 1L;
        Long userId = 3L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Assertions.assertThatThrownBy(() -> bookingService.getBookingDetails(bookingId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Попытка получениие данных другим пользователем.");

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void getBookingDetailsInvalidBookingTest() {
        Long bookingId = 2L;
        Long userId = 1L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> bookingService.getBookingDetails(bookingId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь не найдена.");

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void findBookingUsersAllStateTest() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;
        Long from = 0L;
        Long size = 10L;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking2);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllBookingsByBooker(user)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findBookingUsers(state, userId, from, size);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllBookingsByBooker(user);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void findBookingUsersInvalidUserTest() {
        StateEnum state = StateEnum.ALL;
        Long userId = 2L;
        Long from = 0L;
        Long size = 10L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.findBookingUsers(state, userId, from, size))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void findBookingUsersInvalidParametersTest() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;
        Long from = -1L;
        Long size = 0L;

        Assertions.assertThatThrownBy(() -> bookingService.findBookingUsers(state, userId, from, size))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неверный формат from или size.");

        Mockito.verifyNoInteractions(userRepository, bookingRepository);
    }

    @Test
    void getOwnerBookingsAllStateTest() {
        Long userId = 1L;
        StateEnum state = StateEnum.ALL;
        Long from = 0L;
        Long size = 10L;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking2);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllBookingsByItem_Owner(user)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getOwnerBookings(userId, state, from, size);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllBookingsByItem_Owner(user);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getOwnerBookingsInvalidUserTest() {
        Long userId = 2L;
        StateEnum state = StateEnum.ALL;
        Long from = 0L;
        Long size = 10L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.getOwnerBookings(userId, state, from, size))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getOwnerBookingsInvalidParametersTest() {
        Long userId = 1L;
        StateEnum state = StateEnum.ALL;
        Long from = -1L;
        Long size = 0L;

        Assertions.assertThatThrownBy(() -> bookingService.getOwnerBookings(userId, state, from, size))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неверный формат from или size.");

        Mockito.verifyNoInteractions(userRepository, bookingRepository);
    }
}

