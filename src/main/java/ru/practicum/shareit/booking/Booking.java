package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Booking {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long item;
    private final Long booker;
    private final Status status;
}
