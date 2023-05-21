package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.StateEnum;
import ru.practicum.shareit.booking.StatusEnum;
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
import java.util.Comparator;
import java.util.List;
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
        Optional<User> userDb = userRepository.findById(ownerId);
        User user = userDb.get();
        if (userDb.isEmpty()) {
            log.info("Не найден пользователь c id={}.", ownerId);
            throw new NotFoundException("Пользователь не найден.");
        }
        Optional<Item> itemDb = itemRepository.findById(bookingDto.getItemId());
        if (itemDb.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", bookingDto.getItemId()));
            return null;
        }
        Item item = itemDb.get();
        if (!item.getAvailable()) {
            throw new ValidationException("Запрос на бронирование отклонен");
        }
        validateDate(bookingDto);
        bookingDto.setStatus(StatusEnum.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        booking.setBooker(user);
        booking.setItem(item);
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
            throw new ValidationException("Попытка редактирования статуса другим пользователем.");
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
        if (bookingOptional.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Бронь с %d не найдена.", bookingId));
            return null;
        }
        Booking booking = bookingOptional.get();
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if ((ownerId.equals(userId)) || (bookerId.equals(userId))) {
            return BookingMapper.toDto(booking);
        } else {
            log.info("Получать данные о бронировании может только создатель/владелец вещи.");
            throw new ValidationException("Попытка получениие данных другим пользователем.");
        }
    }

    @Override
    public List<BookingDto> findBookingUsers(String state, Long userId) {
        Optional<User> booker = userRepository.findById(userId);
        if (booker.isEmpty()) {
            log.info("Не найден пользователь c id={}.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        List<Booking> bookingList = bookingRepository.findByBooker_Id(userId);
        StateEnum status = StateEnum.valueOf(state);
        LocalDateTime currentDate = LocalDateTime.now();
        List<Booking> result;

        switch (status) {
            case CURRENT:
                result = bookingList.stream()
                        .filter(booking -> booking.getStart().isBefore(currentDate)
                                && booking.getEnd().isAfter(currentDate)
                                && booking.getStatus().equals(StatusEnum.APPROVED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case PAST:
                result = bookingList.stream()
                        .filter(booking -> booking.getEnd().isBefore(currentDate))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                result = bookingList.stream()
                        .filter(booking -> booking.getStart().isAfter(currentDate))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                result = bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(StatusEnum.WAITING)
                                && booking.getStart().isBefore(currentDate)
                                && booking.getEnd().isAfter(currentDate))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                result = bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(StatusEnum.REJECTED) || booking.getStatus().equals(StatusEnum.CANCELED))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return result.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
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
