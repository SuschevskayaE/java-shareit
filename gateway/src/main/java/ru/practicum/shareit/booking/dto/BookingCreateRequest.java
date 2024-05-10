package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingCreateRequest {

    @NotNull
    @FutureOrPresent
    private final LocalDateTime start;

    @NotNull
    @Future
    private final LocalDateTime end;

    @NotNull
    private final Long itemId;
}
