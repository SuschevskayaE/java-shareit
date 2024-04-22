package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
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
                                            @RequestParam(defaultValue = "ALL", required = false) State state) {
        return service.getUserAll(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) State state) {
        return service.getOwnerAll(userId, state);
    }
}
