package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingItemMapper;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingItemMapper bookingItemMapper;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDtoForBooking itemDtoForBooking;
    private Item item2;
    private Long itemId;
    private Long ownerId;
    private List<Booking> bookings;
    private List<Comment> comments;

    @BeforeEach
    void start() {


        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        itemDto = new ItemDto();
        itemDto.setName("лампочка");
        itemDto.setDescription("яркая лампочка");
        itemDto.setAvailable(true);

        item = Item.builder()
                .id(1L)
                .name("лампочка")
                .description("яркая лампочка")
                .available(true)
                .owner(user)
                .build();

        itemDtoForBooking = new ItemDtoForBooking();
        itemDtoForBooking.setId(1L);
        itemDtoForBooking.setName("лампочка");
        itemDtoForBooking.setDescription("яркая лампочка");
        itemDtoForBooking.setAvailable(true);

        itemDtoForBooking.setComments(new ArrayList<>());

        item2 = Item.builder()
                .id(2L)
                .name("утюг")
                .description("электрический утюг")
                .available(false)
                .owner(user)
                .build();
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createItem = itemService.createItem(itemDto, 1L);


        Assertions.assertThat(createItem)
                .isNotNull()
                .usingRecursiveComparison();

        Assertions.assertThat(createItem.getRequestId()).isNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createItemExceptionTest() {
        when(userRepository.findById(99L)).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.createItem(itemDto, 99L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(itemRepository, itemRequestRepository);
    }

    @Test
    void updateItem() {
        ItemDto itemUpdate = new ItemDto();
        itemUpdate.setId(1L);
        itemUpdate.setName("itemUpdate");
        itemUpdate.setDescription("update description");
        itemUpdate.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updated = itemService.updateItem(1L, 1L, itemUpdate);

        Assertions.assertThat(updated.getName()).isEqualTo("itemUpdate");
        Assertions.assertThat(updated.getDescription()).isEqualTo("update description");
        Assertions.assertThat(updated.getAvailable()).isEqualTo(false);

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItemExceptionTest() {
        ItemDto itemUpdate = new ItemDto();
        itemUpdate.setId(1L);
        itemUpdate.setName("itemUpdate");
        itemUpdate.setDescription("update description");
        itemUpdate.setAvailable(false);

        when(itemRepository.findById(99L)).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(99L, 1L, itemUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        verify(itemRepository, times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findItemByIdTest() {
        itemId = 1L;
        ownerId = 1L;
        bookings = new ArrayList<>();
        comments = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(now.minusDays(1))
                .end(now.minusHours(12))
                .status(StatusEnum.APPROVED)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .status(StatusEnum.APPROVED)
                .build();
        bookings.add(lastBooking);
        bookings.add(nextBooking);
        item.setBookings(bookings);
        item.setComments(comments);
        BookingDtoItem last = new BookingDtoItem();
        last.setId(1L);
        last.setStart(now.minusDays(1));
        last.setEnd(now.minusHours(12));
        last.setStatus(StatusEnum.APPROVED);

        BookingDtoItem next = new BookingDtoItem();
        next.setId(2L);
        next.setStart(now.plusHours(1));
        next.setEnd(now.plusHours(2));
        next.setStatus(StatusEnum.APPROVED);

        item.setBookings(bookings);
        item.setComments(comments);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingItemMapper.toDto(eq(lastBooking))).thenReturn(last);
        when(bookingItemMapper.toDto(eq(nextBooking))).thenReturn(next);

        ItemDtoForBooking result = itemService.findItemById(itemId, ownerId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(itemId);
        Assertions.assertThat(result.getName()).isEqualTo(item.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(result.getComments()).isEmpty();
        Assertions.assertThat(result.getLastBooking()).isNotNull();
        Assertions.assertThat(result.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        Assertions.assertThat(result.getNextBooking()).isNotNull();
        Assertions.assertThat(result.getNextBooking().getId()).isEqualTo(nextBooking.getId());

        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).findById(ownerId);
        Mockito.verifyNoMoreInteractions(itemRepository, userRepository);
    }

    @Test
    void findByIdExceptionTest() {

        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> itemService.findItemById(99L, 1L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Вещь с id %d не найдена.", 99L));

        verify(itemRepository, times(1)).findById(99L);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsTest() {
        ownerId = 1L;
        int from = 0;
        int size = 10;

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        bookings = new ArrayList<>();
        comments = new ArrayList<>();
        List<Comment> comments2 = new ArrayList<>();
        Comment comment = new Comment(1L, "комментарий", item, user, LocalDateTime.now());
        Comment comment2 = new Comment(2L, "комментарий2", item2, user, LocalDateTime.now());
        comments.add(comment);
        comments2.add(comment2);
        item.setBookings(bookings);
        item2.setBookings(bookings);
        item.setComments(comments);
        item2.setComments(comments2);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        when(itemRepository.findAllByOwnerOrderById(user, pageRequest)).thenReturn(items);

        List<ItemDtoForBooking> result = itemService.getAllItems(ownerId, from, size);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(result.get(0).getName()).isEqualTo("лампочка");
        Assertions.assertThat(result.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(result.get(1).getName()).isEqualTo("утюг");

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRepository, times(1)).findAllByOwnerOrderById(user, pageRequest);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingItemMapper, commentRepository);
    }

    @Test
    void getAllItemsValidationExceptionTest() {
        ownerId = 1L;
        int from = -1;
        int size = 0;

        Assertions.assertThatThrownBy(() -> itemService.getAllItems(ownerId, from, size))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неверный формат from или size.");

        Mockito.verifyNoInteractions(userRepository, itemRepository, bookingItemMapper, commentRepository);
    }

    @Test
    void getAllItemsNotFoundExceptionTest() {
        ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> itemService.getAllItems(ownerId, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        verify(userRepository, times(1)).findById(ownerId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void searchForItemByDescriptionTest() {
        item2.setAvailable(true);
        List<Item> items = List.of(item2);
        String text = "утюг";
        int page = 0 / 10;
        PageRequest pageRequest = PageRequest.of(page, 10);
        when(itemRepository.searchItemsByDescription(text, pageRequest)).thenReturn(items);

        List<ItemDto> itemDtos = itemService.searchForItemByDescription(text, 1L, 0, 10);

        Assertions.assertThat(itemDtos)
                .hasSize(1);

        verify(itemRepository, times(1))
                .searchItemsByDescription(text, pageRequest);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void searchForItemByDescriptionValidationExceptionTest() {
        Assertions.assertThatThrownBy(() -> itemService.searchForItemByDescription("дрель", 1L, -1, 10))
                .isInstanceOf(ValidationException.class);

        Mockito.verifyNoInteractions(itemRepository, itemMapper);
    }

    @Test
    void searchForItemByDescriptionReturnsEmptyTest() {
        String text = "дрель";

        List<Item> items = new ArrayList<>();

        int page = 0 / 10;

        PageRequest pageRequest = PageRequest.of(page, 10);
        when(itemRepository.searchItemsByDescription(text, pageRequest)).thenReturn(items);

        List<ItemDto> result = itemService.searchForItemByDescription(text, 1L, 0, 10);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();

        verify(itemRepository, times(1)).searchItemsByDescription(text, pageRequest);
        Mockito.verifyNoMoreInteractions(itemRepository, itemMapper);
    }

    @Test
    void createCommentTest2() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto();
        commentDto.setText("комментарий");

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        Booking booking = Booking.builder()
                .id(1L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .status(StatusEnum.APPROVED)
                .item(item)
                .booker(user)
                .build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        item.setBookings(bookings);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setId(1L);
            return savedComment;
        });

        CommentDto result = itemService.createComment(commentDto, 1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(commentDto.getText(), result.getText());

        verify(commentRepository, times(1)).save(any(Comment.class));
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void createCommentEmptyTextValidationExceptionTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");
        Long itemId = 1L;
        Long userId = 1L;

        assertThrows(ValidationException.class, () ->
                itemService.createComment(commentDto, itemId, userId)
        );

        Mockito.verifyNoInteractions(userRepository, itemRepository, commentRepository);
    }

    @Test
    void createCommentUserNotFoundExceptionTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("комментарий");
        Long itemId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.createComment(commentDto, itemId, userId)
        );

        verify(userRepository, times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, commentRepository);
    }

    @Test
    void createCommentNotFoundExceptionTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("комментарий");
        Long itemId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.createComment(commentDto, itemId, userId)
        );

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, commentRepository);
    }

    @Test
    void createCommentValidationException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("комментарий");
        Long itemId = 1L;
        Long userId = 1L;
        User user = new User();
        Item item = new Item();
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.createComment(commentDto, itemId, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, commentRepository);
    }

    @Test
    public void updateItem_NullOwner_ThrowsValidationException() {
        Long id = 1L;
        Long owner = null;
        ItemDto itemDto = new ItemDto();

        assertThrows(ValidationException.class, () -> {
            itemService.updateItem(id, owner, itemDto);
        });
    }

    @Test
    public void updateItem_ItemNotFound_ThrowsNotFoundException() {
        Long id = 1L;
        Long owner = 2L;
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(id, owner, itemDto);
        });
    }

    @Test
    public void updateItem_OwnerMismatch_ThrowsNotFoundException() {
        Long id = 1L;
        Long owner = 2L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        User itemOwner = new User();
        itemOwner.setId(3L);
        item.setOwner(itemOwner);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(id, owner, itemDto);
        });
    }

    @Test
    public void validate_NullName_ThrowsValidationException() {
        Item item = new Item();
        item.setDescription("Description");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemService.validate(item);
        });
    }

    @Test
    public void validate_BlankName_ThrowsValidationException() {
        Item item = new Item();
        item.setName("");
        item.setDescription("Description");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemService.validate(item);
        });
    }

    @Test
    public void validate_NullDescription_ThrowsValidationException() {
        Item item = new Item();
        item.setName("Name");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemService.validate(item);
        });
    }

    @Test
    public void validate_BlankDescription_ThrowsValidationException() {
        Item item = new Item();
        item.setName("Name");
        item.setDescription("");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemService.validate(item);
        });
    }

    @Test
    public void validate_NullAvailable_ThrowsValidationException() {
        Item item = new Item();
        item.setName("Name");
        item.setDescription("Description");

        assertThrows(ValidationException.class, () -> {
            itemService.validate(item);
        });
    }

}
