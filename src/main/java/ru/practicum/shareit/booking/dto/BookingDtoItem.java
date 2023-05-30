package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Enum.StatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BookingDtoItem {
    private Long id;
    @JsonAlias({"start"})
    private LocalDateTime start;
    @JsonAlias({"end"})
    private LocalDateTime end;
    private Long bookerId;
    private StatusEnum status;
}
