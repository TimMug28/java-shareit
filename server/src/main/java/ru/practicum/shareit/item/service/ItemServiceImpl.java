package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingItemMapper;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperBooking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingItemMapper bookingItemMapper;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long owner) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> getUser = userRepository.findById(owner);
        if (getUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User user = getUser.get();
        item.setOwner(user);
        item.setRequestId(itemDto.getRequestId());
        Item createItem = itemRepository.save(item);
        ItemDto createdItemDto = ItemMapper.toItemDto(createItem);
        return createdItemDto;
    }

    @Override
    public ItemDtoForBooking findItemById(Long id, Long ownerId) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Вещь с id %d не найдена.", id));
            return null;
        }
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        Item item = itemOptional.get();
        List<Booking> bookings = item.getBookings();
        LocalDateTime localDateTime = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        ItemDtoForBooking itemDtoForBooking = ItemMapperBooking.toDto(item);
        if (Objects.equals(item.getOwner().getId(), ownerId) && bookings != null) {
            nextBooking = findNext(bookings, localDateTime);
            lastBooking = findLast(bookings, localDateTime);
        }
        itemDtoForBooking.setLastBooking(lastBooking != null
                ? bookingItemMapper.toDto(lastBooking) : null);
        itemDtoForBooking.setNextBooking(nextBooking != null
                ? bookingItemMapper.toDto(nextBooking) : null);
        List<Comment> comments = item.getComments();
        List<CommentDto> commentDto = comments.stream()
                .map(CommentMapper::toDTO).collect(Collectors.toList());
        itemDtoForBooking.setComments(commentDto);
        return itemDtoForBooking;
    }

    @Override
    public ItemDto updateItem(Long id, Long owner, ItemDto itemDto) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", id));
            return null;
        }
        Item itemOld = item.get();
        if (!Objects.equals(itemOld.getOwner().getId(), owner)) {
            throw new NotFoundException("Редактировать вещь может только её владелец.");
        }
        if (itemDto.getName() != null) {
            itemOld.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemOld.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemOld.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            itemOld.setRequestId(itemDto.getRequestId());
        }
        ItemDto updateItem = ItemMapper.toItemDto(itemRepository.save(itemOld));
        log.info("Отредактирована вещь c id={}.", id);
        return updateItem;
    }

    @Override
    public List<ItemDtoForBooking> getAllItems(Long ownerId, int from, int size) {
        Optional<User> userOptional = userRepository.findById(ownerId);
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь c id={}.", ownerId);
            throw new NotFoundException("Пользователь не найден.");
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Item> paginatedItems = itemRepository.findAllByOwnerOrderById(userOptional.get(), pageRequest);

        List<ItemDtoForBooking> itemDtoForBookingSet = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (Item item : paginatedItems) {
            ItemDtoForBooking itemDtoForBooking = ItemMapperBooking.toDto(item);
            List<Booking> bookings = item.getBookings();
            Booking lastBooking = findLast(bookings, localDateTime);
            Booking nextBooking = findNext(bookings, localDateTime);
            itemDtoForBooking.setLastBooking(lastBooking != null
                    ? bookingItemMapper.toDto(lastBooking) : null);
            itemDtoForBooking.setNextBooking(nextBooking != null
                    ? bookingItemMapper.toDto(nextBooking) : null);

            List<Comment> comments = item.getComments();
            List<CommentDto> commentDto = comments.stream()
                    .map(CommentMapper::toDTO).collect(Collectors.toList());
            itemDtoForBooking.setComments(commentDto);
            itemDtoForBookingSet.add(itemDtoForBooking);
        }
        return itemDtoForBookingSet;
    }

    @Override
    public List<ItemDto> searchForItemByDescription(String text, Long owner, int from, int size) {
        String description = text.toLowerCase();
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        List<Item> itemList = itemRepository.searchItemsByDescription(text, pageRequest);
        log.info("Запрошены вещи по ключевому слову={}.", description);

        return itemList
                .stream()
                .map(ItemMapper::toItemDto)
                .filter(ItemDto::getAvailable)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь c id={}.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        User user = userOptional.get();
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.info("Не найдена вещь c id={}.", itemId);
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", itemId));
        }
        Item item = itemOptional.get();
        if (item.getOwner().equals(user)) {
            throw new ValidationException("Владелец вещи не может оставлять комментарии.");
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        commentDto.setCreatedDate(localDateTime);
        List<Booking> bookings = item.getBookings();
        Comment comment = CommentMapper.toComment(commentDto, user);
        CommentDto commentD;

        boolean condition = bookings.stream()
                .filter(b -> b.getBooker().getId().equals(userId))
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now()));

        if (!condition) {
            throw new ValidationException("Ошибка при сохранении комментария.");
        }
        comment.setItem(item);
        comment.setAuthorName(user);
        commentD = CommentMapper.toDTO(commentRepository.save(comment));
        return commentD;
    }


    private Booking findNext(List<Booking> bookings, LocalDateTime localDateTime) {
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(localDateTime))
                .filter(b -> b.getStatus() == StatusEnum.APPROVED || b.getStatus() == StatusEnum.WAITING)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking findLast(List<Booking> bookings, LocalDateTime localDateTime) {
        return bookings.stream()
                .filter(b -> b.getStart().isBefore(localDateTime))
                .filter(b -> b.getStatus() == StatusEnum.APPROVED)
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }
}
