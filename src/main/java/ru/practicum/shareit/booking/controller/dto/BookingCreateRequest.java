package ru.practicum.shareit.booking.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingCreateRequest {

    @NotNull
    @Future
    private final LocalDateTime start;

    @NotNull
    @Future
    private final LocalDateTime end;

    @NotNull
    private final Long itemId;

}
