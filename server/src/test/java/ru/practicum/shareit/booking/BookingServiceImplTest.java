package ru.practicum.shareit.booking;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    PageRequest pageRequest;
    private int from;
    private int size;

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

        from = 0;
        size = 10;
        int page = from / size;
        pageRequest = PageRequest.of(page, size);
    }

    @Test
    void createBookingTest() {
        bookingDto.setItemId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto createdBookingDto = bookingService.createBooking(bookingDto, 1L);
        assertThat(createdBookingDto).isNotNull();
        assertThat(createdBookingDto.getItemId()).isEqualTo(1L);
        assertThat(createdBookingDto.getStatus()).isEqualTo(StatusEnum.WAITING);

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(itemRepository, Mockito.times(1)).findById(1L);
        verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void createBookingExceptionTest() {
        when(userRepository.findById(eq(99L)))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> bookingService.createBooking(bookingDto, 99L));

        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        verify(userRepository, times(1)).findById(eq(99L));
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

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(StatusEnum.APPROVED);

        verify(userRepository, Mockito.times(1)).findById(ownerId);
        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
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

        verify(userRepository, Mockito.times(1)).findById(ownerId);
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

        verify(userRepository, Mockito.times(1)).findById(ownerId);
        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
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

        verify(userRepository, Mockito.times(1)).findById(ownerId);
        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getBookingDetailsTest() {
        Long bookingId = 1L;
        Long userId = 1L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BookingDto result = bookingService.getBookingDetails(bookingId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);

        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        verify(userRepository, Mockito.times(1)).findById(userId);
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

        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        verify(userRepository, Mockito.times(1)).findById(userId);
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

        verify(bookingRepository, Mockito.times(1)).findById(bookingId);
        verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(bookingRepository, userRepository);
    }

    @Test
    void findBookingUsersAllStateTest() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking2);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllBookingsByBooker(user, pageRequest)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findBookingUsers(state, userId, from, size);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(userRepository, Mockito.times(1)).findById(userId);
        verify(bookingRepository, Mockito.times(1)).findAllBookingsByBooker(user, pageRequest);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void findBookingUsers_AllState_ReturnsAllBookingsByBooker() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(user);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(user);
        Item item1 = new Item();
        item1.setId(10L);
        booking1.setItem(item1);

        Item item2 = new Item();
        item2.setId(20L);
        booking2.setItem(item2);

        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingsByBooker(user, pageRequest)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findBookingUsers(state, userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(10L, result.get(0).getItem().getId());
        assertEquals(20L, result.get(1).getItem().getId());

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllBookingsByBooker(user, pageRequest);
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void findBookingUsers_WaitingState_ReturnsWaitingBookingsByBooker() {
        StateEnum state = StateEnum.WAITING;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(StatusEnum.WAITING);
        booking1.setBooker(user);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(StatusEnum.WAITING);
        booking2.setBooker(user);
        Item item1 = new Item();
        item1.setId(10L);
        booking1.setItem(item1);

        Item item2 = new Item();
        item2.setId(20L);
        booking2.setItem(item2);

        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.WAITING, pageRequest)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findBookingUsers(state, userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(10L, result.get(0).getItem().getId());
        assertEquals(20L, result.get(1).getItem().getId());

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.WAITING, pageRequest);
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void findBookingUsers_RejectedState_ReturnsRejectedBookingsByBooker() {
        StateEnum state = StateEnum.REJECTED;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(StatusEnum.REJECTED);
        booking1.setBooker(user);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(StatusEnum.REJECTED);
        booking2.setBooker(user);
        Item item1 = new Item();
        item1.setId(10L);
        booking1.setItem(item1);

        Item item2 = new Item();
        item2.setId(20L);
        booking2.setItem(item2);

        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.REJECTED, pageRequest)).thenReturn(bookings);

        List<BookingDto> result = bookingService.findBookingUsers(state, userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(10L, result.get(0).getItem().getId());
        assertEquals(20L, result.get(1).getItem().getId());

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.REJECTED, pageRequest);
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void findBookingUsers_InvalidUserId_ThrowsNotFoundException() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingUsers(state, userId, from, size));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void findBookingUsers_UserNotFound_ThrowsNotFoundException() {
        StateEnum state = StateEnum.ALL;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findBookingUsers(state, userId, from, size));

        verify(userRepository).findById(userId);
    }


    @Test
    void findBookingUsersInvalidUserTest() {
        StateEnum state = StateEnum.ALL;
        Long userId = 2L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.findBookingUsers(state, userId, from, size))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }


    @Test
    void getOwnerBookingsAllStateTest() {
        Long userId = 1L;
        StateEnum state = StateEnum.ALL;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking2);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllBookingsByItem_Owner(user, pageRequest)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(userRepository, Mockito.times(1)).findById(userId);
        verify(bookingRepository, Mockito.times(1)).findAllBookingsByItem_Owner(user, pageRequest);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getOwnerBookingsInvalidUserTest() {
        Long userId = 2L;
        StateEnum state = StateEnum.ALL;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookingService.getOwnerBookings(userId, state, from, size))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    public void createBooking_NonexistentUser_ThrowsNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        Long ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDto, ownerId);
        });
    }

    @Test
    public void createBooking_NonexistentItem_ThrowsNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDto, ownerId);
        });
    }


    @Test
    public void createBooking_BookingOwnItem_ThrowsNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        Long ownerId = 1L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDto, ownerId);
        });
    }
}

