package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long owner) {
        Item item = ItemMapper.toItem(itemDto);
        if (owner == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
        validate(item);
        User user = UserMapper.toUser(userService.findUserById(owner));
        item.setOwner(user);
        ItemDto createdItem = ItemMapper.toItemDto(itemRepository.save(item));
        log.info("Добавлена новая вещь: {}", createdItem);

        return createdItem;
    }

    @Override
    public ItemDto findItemById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id вещи не должно быть null.");
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", id));
            return null;
        }
        log.info("Запрошена вещь c id={}.", id);
        return ItemMapper.toItemDto(item.get());
    }

    @Override
    public ItemDto updateItem(Long id, Long owner, ItemDto itemDto) {
        if (owner == null) {
            throw new ValidationException("Не указан владелец вещи.");
        }
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
        if (itemDto.getRequest() != null) {
            itemOld.setRequest(itemDto.getRequest());
        }
        ItemDto updateItem = ItemMapper.toItemDto(itemRepository.save(itemOld));
        log.info("Отредактирована вещь c id={}.", id);
        return updateItem;
    }

    @Override
    public List<ItemDto> getAllItems(Long owner) {
        User user = UserMapper.toUser(userService.findUserById(owner));
        List<Item> itemList = itemRepository.findAllByOwnerOrderById(user);
        log.info("Получен список всех вещей пользователя с id = " + user.getId());
        return itemList
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Set<ItemDto> searchForItemByDescription(String text, Long owner) {
        String description = text.toLowerCase();
        Set<Item> itemList = itemRepository.searchItemsByDescription(text);
        log.info("Запрошены вещи по ключевому слову={}.", description);
        return itemList
                .stream()
                .map(ItemMapper::toItemDto)
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toSet());
    }

    @Override
    public List<ItemDto> findItemsByUserId(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Ошибка при получении списка вещей пользователя с ID = " + ownerId
                        + "в БД. В БД отсутствует запись о пользователе."));
        List<Item> items = itemRepository.findAllByOwnerOrderById(owner);
        List <ItemDto> result = new ArrayList<>();
        for (Item i : items){
            result.add(ItemMapper.toItemDto(i));
        }
        return result;
    }

    private void validate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.info("Пустое поле name.");
            throw new ValidationException("Поле name не может быть пустым.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.info("Пустое поле description.");
            throw new ValidationException("Поле description не может быть пустым.");
        }
        if (item.getAvailable() == null) {
            log.info("Пустое поле available.");
            throw new ValidationException("Поле available не может быть пустым.");
        }
    }
}
