package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemCreateRequest request) {
        return service.create(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody ItemUpdateRequest request) {
        return service.update(userId, itemId, request);
    }

    @GetMapping("/{itemId}")
    public ItemUserCommentsResponse getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemUserCommentsResponse> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getUsersAll(userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        return service.getAll(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentCreateRequest request) {
        return service.createComment(userId, itemId, request);
    }
}
