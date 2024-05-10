package ru.practicum.shareit.booking.enums;

import lombok.Getter;

@Getter
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
