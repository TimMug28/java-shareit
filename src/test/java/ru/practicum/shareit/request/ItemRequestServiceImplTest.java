package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    ItemRequestDto itemRequestDto;
    User requestor;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("описание");

        requestor = new User();
        requestor.setId(1L);
        requestor.setName("user");
        requestor.setEmail("user@yandex.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("описание");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(new ArrayList<>());
    }

    @Test
    void createdItemRequestTest() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto mustBe = ItemRequestMapper.toItemRequestDto(itemRequest);

        ItemRequestDto saved = itemRequestService.createRequest(itemRequestDto, 1L);

        Assertions.assertThat(saved)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mustBe);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void createdItemRequestExceptionTest() {

        Mockito.when(userRepository.findById(99L)).thenThrow(new NotFoundException("Пользователь не найден."));

        Throwable throwable = Assertions.catchException(() -> itemRequestService.createRequest(itemRequestDto, 99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");
    }

    @Test
    void getAllItemRequestTest() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("описание 1");
        itemRequest1.setRequestor(requestor);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("описание 2");
        itemRequest2.setRequestor(requestor);

        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest1);
        itemRequestList.add(itemRequest2);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.findAllByRequestorOrderByIdDesc(requestor)).thenReturn(itemRequestList);

        List<ItemRequestDto> expected = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        ;
        List<ItemRequestDto> result = itemRequestService.getAllItemRequest(1L);

        Assertions.assertThat(result)
                .isNotNull()
                .hasSameSizeAs(expected)
                .containsExactlyElementsOf(expected);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByRequestorOrderByIdDesc(requestor);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void getAllItemRequestNotFoundExceptionTest() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() -> itemRequestService.getAllItemRequest(99L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(99L);
        Mockito.verifyNoInteractions(itemRequestRepository);
    }


    @Test
    void getItemRequestByIdTest() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto mustBe = ItemRequestMapper.toItemRequestDto(itemRequest);

        ItemRequestDto returned = itemRequestService.getItemRequestById(1L, 1L);

        Assertions.assertThat(returned)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(mustBe);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void getItemRequestByIdNotFoundExceptionTest() {

        Mockito.when(userRepository.findById(99L)).thenThrow(new NotFoundException("Пользователь не найден."));

        Throwable throwable = Assertions.catchException(() -> itemRequestService.getItemRequestById(99L, 1L));

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllRequestOtherUsersTest() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("описание 1");
        itemRequest1.setRequestor(requestor);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("описание 2");
        itemRequest2.setRequestor(requestor);

        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest1);
        itemRequestList.add(itemRequest2);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.findAllByRequestorNotOrderByIdDesc(requestor)).thenReturn(itemRequestList);

        List<ItemRequestDto> expectedResult = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        List<ItemRequestDto> result = itemRequestService.getAllRequestOtherUsers(1L, 0L, 5L);

        Assertions.assertThat(result)
                .isNotNull()
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByRequestorNotOrderByIdDesc(requestor);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void getAllRequestOtherUsersValidationExceptionTest() {
        Throwable throwable = Assertions.catchThrowable(() ->
                itemRequestService.getAllRequestOtherUsers(1L, -1L, 0L)
        );

        Assertions.assertThat(throwable)
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неверный формат from или size.");

        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void getAllRequestOtherUsersNotFoundExceptionTest() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = Assertions.catchThrowable(() ->
                itemRequestService.getAllRequestOtherUsers(1L, 0L, 5L)
        );

        Assertions.assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }
}
