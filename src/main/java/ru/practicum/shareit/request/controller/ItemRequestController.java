package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestCreateRequest request) {
        return service.create(userId, request);
    }

    @GetMapping
    public List<ItemRequestResponse> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getUsersAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        final Pageable pageable = FromSizeRequest.of(from, size, sort);
        return service.getAll(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long requestId) {
        return service.get(userId, requestId);
    }


}
