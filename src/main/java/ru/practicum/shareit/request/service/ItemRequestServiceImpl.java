package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
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
import java.util.stream.Collectors;

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
        User requestor = userOptional.get();
        List <ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorOrderByIdDesc(requestor);
        List<ItemRequestDto> itemRequestDto = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequestOtherUsers(Long requesterId, Long from, Long size) {
        if (size == 0|| from < 0 || size < 0){
            log.info("Неверный формат from или size.");
            throw new ValidationException("Неверный формат from или size.");
        }
        if (requesterId == null || requesterId < 0) {
            log.info("Пустое поле id.");
            throw new ValidationException("Поле id не может быть пустым или отрицательным.");
        }
        Optional<User> userOptional = userRepository.findById(requesterId);
        if (userOptional.isEmpty()) {
            log.info("Не найден пользователь с id:" + requesterId);
            throw new NotFoundException("Пользователь не найден.");
        }
        User requestor = userOptional.get();
        List <ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorNotOrderByIdDesc(requestor);
        int startIndex = from.intValue();
        int endIndex = Math.min(startIndex + size.intValue(), itemRequestList.size());

        List<ItemRequestDto> itemRequestDtoList = itemRequestList.subList(startIndex, endIndex)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        return itemRequestDtoList;
    }

    private void validate(ItemRequestDto itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            log.info("Пустое поле description.");
            throw new ValidationException("Поле description не может быть пустым.");
        }
    }

}
