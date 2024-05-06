package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.FromSizeRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingCreateRequest request) {
        return service.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approved(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(defaultValue = "false") Boolean approved) {
        return service.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId) {
        return service.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> getUserAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL", required = false) State state,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        return service.getUserAll(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) State state,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        return service.getOwnerAll(userId, state, pageable);
    }
}
