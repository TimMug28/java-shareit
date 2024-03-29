package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Enum.StateEnum;
import ru.practicum.shareit.booking.Enum.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingDto createdBookingDto;
    private Long ownerId;
    private Long bookingId;
    private Long userId;
    private int from;
    private int size;

    @BeforeEach
    void start() {
        bookingId = 1L;
        ownerId = 1L;
        userId = 1L;
        from = 0;
        size = 10;

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,10,5,16,23));
        bookingDto.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto.setStatus(StatusEnum.APPROVED);

        createdBookingDto = new BookingDto();
        createdBookingDto.setId(1L);
        createdBookingDto.setItemId(1L);
        createdBookingDto.setStart(LocalDateTime.of(2023,10,5,16,23));
        createdBookingDto.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        createdBookingDto.setStatus(StatusEnum.APPROVED);
    }

    @Test
    public void createBookingTest() throws Exception {

        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(createdBookingDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void updateBookingStatusTest() throws Exception {
        boolean approved = true;

        BookingDto updatedBookingDto = new BookingDto();
        updatedBookingDto.setId(bookingId);
        updatedBookingDto.setStatus(StatusEnum.REJECTED);

        when(bookingService.updateBookingStatus(bookingId, approved, ownerId)).thenReturn(updatedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(bookingService, times(1)).updateBookingStatus(bookingId, approved, ownerId);
    }

    @Test
    public void getBookingDetailsTest() throws Exception {

        bookingDto.setId(bookingId);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,10,5,16,23));
        bookingDto.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto.setStatus(StatusEnum.APPROVED);

        when(bookingService.getBookingDetails(bookingId, userId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).getBookingDetails(bookingId, userId);
    }

    @Test
    public void findBookingUsersTest() throws Exception {
        String state = "ALL";

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2023,10,5,16,23));
        bookingDto1.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto1.setStatus(StatusEnum.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto2.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(2));
        bookingDto2.setStatus(StatusEnum.WAITING);

        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.findBookingUsers(eq(StateEnum.ALL), eq(userId), eq(from), eq(size)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("WAITING"));

        verify(bookingService, times(1)).findBookingUsers(eq(StateEnum.ALL), eq(userId), eq(from), eq(size));
    }

    @Test
    public void getOwnerBookingsTest() throws Exception {
        String state = "ALL";

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2023,10,5,16,23));
        bookingDto1.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto1.setStatus(StatusEnum.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.of(2023,10,5,16,23).plusHours(1));
        bookingDto2.setEnd(LocalDateTime.of(2023,10,5,16,23).plusHours(2));
        bookingDto2.setStatus(StatusEnum.WAITING);

        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getOwnerBookings(eq(userId), eq(StateEnum.ALL), eq(from), eq(size)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("WAITING"));

        verify(bookingService, times(1)).getOwnerBookings(eq(userId), eq(StateEnum.ALL), eq(from), eq(size));
    }
}
