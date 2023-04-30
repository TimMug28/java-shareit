package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
public class ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto createItem(ItemDto itemDto, Integer owner) {
        itemDto.setOwner(owner);
        validate(ItemMapper.toItem(itemDto));
        validateUser(owner);
        Item item = ItemMapper.toItem(itemDto);
        ItemDto createdItem = ItemMapper.toItemDto(itemRepository.createItem(item));
        log.info("Добавлена новая вещь: {}", createdItem);
        return createdItem;
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
