package ru.practicum.shareit.booking.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingCreateRequest {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;

}
