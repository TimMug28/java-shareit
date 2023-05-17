package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserMapper;
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
        if (owner == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
        User user = UserMapper.toUser(userService.findUserById(owner));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Запрос на бронирование отклонен");
        }
        if(bookingDto.getStart() == bookingDto.getEnd()){
            log.info("Время начала и окончания бронирования не должны совпадать.");
            throw new ValidationException("Время начала и окончания бронирования совпадают.");
        }
        bookingDto.setStatus(StatusEnum.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(newBooking);
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
