package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long ownerId) {
        if (ownerId == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
        Optional<User> userOptional = userRepository.findById(ownerId);
        User booker = userOptional.get();
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь c id={}.", ownerId);
            throw new NotFoundException("Пользователь не найден.");
        }
        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        if (itemOptional.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", bookingDto.getItemId()));
            return null;
        }
        Item item = itemOptional.get();
        if (item.getOwner().equals(booker)) {
            log.info("Нельзя бронировать свои вещи.");
            throw new NotFoundException("Попытка бронирования своих вещей.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Запрос на бронирование отклонен");
        }
        validateDate(bookingDto);
        bookingDto.setStatus(StatusEnum.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(newBooking);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long ownerId) {
        Optional<User> userDb = userRepository.findById(ownerId);
        if (userDb.isEmpty()) {
            log.info("Не найден пользователь c id={}.", ownerId);
            throw new NotFoundException("Пользователь не найден.");
        }
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Бронь с %d не найдена.", bookingId));
            return null;
        }
        Booking oldBooking = bookingOptional.get();
        if (!oldBooking.getItem().getOwner().getId().equals(ownerId)) {
            log.info("Изменять статус бронирования может только владелец вещи.");
            throw new NotFoundException("Попытка редактирования статуса другим пользователем.");
        }
        if (Objects.equals(StatusEnum.APPROVED, oldBooking.getStatus()) && approved) {
            log.info("Статус бронирования уже изменён.");
            throw new ValidationException("Попытка изменения статуса бронирования.");
        }
        Booking booking = bookingOptional.get();
        if (approved) {
            booking.setStatus(StatusEnum.APPROVED);
        } else {
            booking.setStatus(StatusEnum.REJECTED);
        }
        Booking updateBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updateBooking);
    }

    @Override
    public BookingDto getBookingDetails(Long bookingId, Long userId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (bookingOptional.isEmpty()) {
            log.info("Не найдена бронь с id:" + bookingId);
            throw new NotFoundException("Бронь не найдена.");
        }
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь с id:" + bookingId);
            throw new NotFoundException("Пользователь не найден.");
        }
        Booking booking = bookingOptional.get();
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if ((ownerId.equals(userId)) || (bookerId.equals(userId))) {
            return BookingMapper.toDto(booking);
        } else {
            log.info("Получать данные о бронировании может только создатель/владелец вещи.");
            throw new NotFoundException("Попытка получениие данных другим пользователем.");
        }
    }

    @Override
    public List<BookingDto> findBookingUsers(StateEnum state, Long userId, Long from, Long size) {
        if (size == 0 || from < 0 || size < 0) {
            log.info("Неверный формат from или size.");
            throw new ValidationException("Неверный формат from или size.");
        }
        Optional<User> booker = userRepository.findById(userId);
        if (booker.isEmpty()) {
            log.info("Не найден пользователь c id={}.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        User user = booker.get();
        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllBookingsByBooker(user);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(user, currentDate);
                break;
            case CURRENT:
                result = bookingRepository.findAllBookingsForBookerWithStartAndEnd(user, currentDate, currentDate);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(user, currentDate);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, StatusEnum.REJECTED);
                break;
            default:
                break;
        }
        int startIndex = from.intValue();
        int endIndex = Math.min(startIndex + size.intValue(), result.size());
        List<Booking> paginatedResult = result.subList(startIndex, endIndex);
        return paginatedResult.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, StateEnum state, Long from, Long size) {
        if (size == 0 || from < 0 || size < 0) {
            log.info("Неверный формат from или size.");
            throw new ValidationException("Неверный формат from или size.");
        }
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            log.info("Не найден пользователь c id={}.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        User user = owner.get();
        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllBookingsByItem_Owner(user);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(user, currentDate);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(user, currentDate, currentDate);
                break;
            case PAST:
                result = bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(user, currentDate);
                break;
            case WAITING:
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, StatusEnum.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, StatusEnum.REJECTED);
                break;
            default:
                break;
        }
        int startIndex = from.intValue();
        int endIndex = Math.min(startIndex + size.intValue(), result.size());
        List<Booking> paginatedResult = result.subList(startIndex, endIndex);
        return paginatedResult.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
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
