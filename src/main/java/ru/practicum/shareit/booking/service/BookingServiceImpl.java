package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long ownerId) {
        if (ownerId == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
        User user = UserMapper.toUser(userService.findUserById(ownerId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Запрос на бронирование отклонен");
        }
        validateDate(bookingDto);
        bookingDto.setStatus(StatusEnum.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
//        item.getBookings().add(booking);
//        itemService.updateItem(item.getId(), ownerId, ItemMapper.toItemDto(item));
        booking.setBooker(user);
        booking.setItem(item);
        Booking newBooking = bookingRepository.save(booking);
//        user.getBookings().add(newBooking);
//        userService.updateUser(ownerId,UserMapper.toUserDto(user));
        return BookingMapper.toDto(newBooking);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long ownerId) {
        User user = UserMapper.toUser(userService.findUserById(ownerId));
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Бронь с %d не найдена.", bookingId));
            return null;
        }
        Booking oldBooking = bookingOptional.get();
        if (!oldBooking.getBooker().getId().equals(ownerId)) {
            log.info("Изменять статус бронирования может только владелец вещи.");
            throw new ValidationException("Попытка редактирования статуса другим пользователем.");
        }
        List<Item> itemList = user.getItems();
        Booking booking = bookingOptional.get();
//        booking.setBooker(user);
        if (approved) {
            booking.setStatus(StatusEnum.APPROVED);
        } else {
            booking.setStatus(StatusEnum.REJECTED);
        }
        Booking updateBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updateBooking);
    }

    @Override
    public List<BookingDto> getBookingDetails(Long bookingId, Long userId) {
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

    private void validateDate(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            log.info("Время начала и окончания бронирования не должны быть пустыми.");
            throw new ValidationException("Время начала и окончания бронирования не должны быть пустыми.");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.info("Время начала и окончания бронирования не должны совпадать или быть в обратнром порядке.");
            throw new ValidationException("Время начала и окончания бронирования совпадают.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now().minusSeconds(1))) {
            log.info("Время начала бронирования не может быть в прошлом.");
            throw new ValidationException("Время начала бронирования не может быть в прошлом.");
        }
    }
}
