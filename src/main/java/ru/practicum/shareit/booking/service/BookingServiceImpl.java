package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long owner) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (owner == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
        UserDto user = userService.findUserById(owner);
        ItemDto itemDto = itemService.findItemById(bookingDto.getItemId());
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Вещь забронирована");
        }

        return null;
    }

    @Override
    public List<BookingDto> findAllBookings(Long bookingId, Long userId) {
        return null;
    }

    @Override
    public BookingDto updateBookingStatus(Long id, boolean approved, Long ownerId) {
        return null;
    }

    @Override
    public List<BookingDto> findBookingUsers(String state, Long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        return null;
    }
}
