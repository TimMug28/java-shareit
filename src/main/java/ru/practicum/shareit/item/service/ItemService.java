package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(ItemDto itemDto, Integer owner) {
        itemDto.setOwner(owner);
        validate(ItemMapper.toItem(itemDto));
        validateUser(owner);
        Item item = ItemMapper.toItem(itemDto);
        ItemDto createdItem = ItemMapper.toItemDto(itemRepository.createItem(item));
        log.info("Добавлена новая вещь: {}", createdItem);
        return createdItem;
    }

    public ItemDto findItemById(Integer id) {
        ValidateUtil.validNumberNotNull(id, "id вещи не должно быть null.");
        Item item = itemRepository.findItemById(id);
        if (item == null) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", id));
            return null;
        }
        log.info("Запрошена вещь c id={}.", id);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Integer id, Integer owner, ItemDto itemDto) {
        if (owner == null) {
            throw new ValidationException("Не указан владелец вещи.");
        }
        ItemDto itemDtoOld = findItemById(id);
        if (itemDtoOld == null) {
            ValidateUtil.throwNotFound(String.format("Вещь с %d не найдена.", id));
            return null;
        }
        if (!Objects.equals(itemDtoOld.getOwner(), owner)) {
            throw new NotFoundException("Редактировать вещь может только её владелец.");
        }
        Item item = ItemMapper.toItem(itemDto);
        validateUser(owner);
        Item updateItem = itemRepository.updateItem(id, item);
        log.info("Отредактирована вещь c id={}.", id);
        return ItemMapper.toItemDto(updateItem);
    }

    public List<ItemDto> getAllItems(Integer owner) {
        validateUser(owner);
        List<ItemDto> itemDtoList = new ArrayList<>();
        List<Item> itemList = itemRepository.getAllItems(owner);
        for (Item item : itemList) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        log.info("Получен список всех вещей.");
        return itemDtoList;
    }

    public Set<ItemDto> searchForItemByDescription(String text, Integer owner) {
        Set<ItemDto> itemDtoList = new LinkedHashSet<>();
        validateUser(owner);
        String description = text.toLowerCase();
        Set<Item> itemList = itemRepository.searchForItemByDescription(description);
        for (Item item : itemList) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        log.info("Запрошены вещи по ключевому слову={}.", description);
        return itemDtoList;
    }

    private void validate(Item item) {
        if (item.getOwner() == null) {
            log.info("Пустое поле owner.");
            throw new ValidationException("Поле owner не может быть пустым.");
        }
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

    private void validateUser(Integer owner) {
        if (userRepository.getUserById(owner) == null) {
            log.info("Пользователь с id: {}, не найден ", owner);
            throw new NotFoundException("Отсутствует пользователь с заданным идентификатором.");
        }
    }
}
