package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
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

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long id) {
        LocalDateTime time = LocalDateTime.now();
        if (id == null || id < 0) {
            throw new ValidationException("Поле id не может быть пустым.");
        }
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        validate(itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, time);
        itemRequest.setRequestor(userOptional.get());
        ItemRequestDto itemRequestDtoCreated = ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
        return itemRequestDtoCreated;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Поле id не может быть пустым или отрицательным.");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User requestor = userOptional.get();
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorOrderByIdDesc(requestor);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequestOtherUsers(Long requesterId, int from, int size) {
        if (size == 0 || from < 0 || size < 0) {
            throw new ValidationException("Неверный формат from или size.");
        }
        if (requesterId == null || requesterId < 0) {
            throw new ValidationException("Поле id не может быть пустым или отрицательным.");
        }
        Optional<User> userOptional = userRepository.findById(requesterId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User requestor = userOptional.get();
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorNotOrderByIdDesc(requestor, pageRequest);
        return itemRequestList
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requesterId, Long requestId) {
        if (requestId == null || requesterId == null) {
            throw new ValidationException("Поле id не может быть пустым.");
        }
        Optional<User> userOptional = userRepository.findById(requesterId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty()) {
            throw new NotFoundException("Запрос не найден.");
        }
        ItemRequest itemRequest = itemRequestOptional.get();
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private void validate(ItemRequestDto itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Поле description не может быть пустым.");
        }
    }
}