package ru.practicum.shareit.booking.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingItemResponse {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;
    private final Long bookerId;
    private final Status status;
}
