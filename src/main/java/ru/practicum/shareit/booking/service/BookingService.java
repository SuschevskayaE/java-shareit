package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingResponse create(Long userId, BookingCreateRequest data);

    BookingResponse approved(Long ownerId, Long bookingId, Boolean approved);

    List<BookingResponse> getUserAll(Long userId, State state, Pageable pageable);

    List<BookingResponse> getOwnerAll(Long userId, State state, Pageable pageable);

    BookingResponse get(long userId, long id);
}
