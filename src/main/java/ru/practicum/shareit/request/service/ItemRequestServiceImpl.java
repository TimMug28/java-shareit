package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private  final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long id) {
        LocalDateTime time = LocalDateTime.now();
        if (id == null || id < 0) {
            log.info("Пустое поле id.");
            throw new ValidationException("Поле id не может быть пустым.");
        }
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь с id:" + id);
            throw new NotFoundException("Пользователь не найден.");
        }
        List <Item> itemOptional = itemRepository.findAllByOwnerOrderById(userOptional.get());
        validate(itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, time);
        itemRequest.setRequestor(userOptional.get());
        ItemRequestDto itemRequestDtoCreated =ItemRequestMapper.toItemRequestDto( itemRequestRepository.save(itemRequest));
        log.info("Добавлен новый запрос: {}", itemRequestDtoCreated.getId());
        return itemRequestDtoCreated;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Long id) {
        if (id == null || id < 0) {
            log.info("Пустое поле id.");
            throw new ValidationException("Поле id не может быть пустым или отрицательным.");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь с id:" + id);
            throw new NotFoundException("Пользователь не найден.");
        }
        List <Item> itemOptional = itemRepository.findAllByOwnerOrderById(userOptional.get());
//        Optional <ItemRequest> itemRequestOptional = itemRequestRepository.
        return null;
    }

    private void validate(ItemRequestDto itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            log.info("Пустое поле description.");
            throw new ValidationException("Поле description не может быть пустым.");
        }
    }

}
