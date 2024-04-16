package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    private final ItemMapper mapper;

    @PostMapping
    public ItemResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemCreateRequest request) {
        Item item = mapper.toItem(request);
        Item modified = service.create(userId, item);
        return mapper.toResponse(modified);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId, @Valid @RequestBody ItemUpdateRequest request) {
        Item item = mapper.toItem(request);
        item.setId(itemId);
        Item modified = service.update(userId, item);
        return mapper.toResponse(modified);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getById(@PathVariable long itemId) {
        Item modified = service.get(itemId);
        return mapper.toResponse(modified);
    }

    @GetMapping
    public List<ItemResponse> getUsersAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAll().stream().filter(i -> i.getOwner().equals(userId)).map(mapper::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        return service.getAll().stream().filter(i -> i.getAvailable().equals(true))
                .filter(i -> (i.getName().toUpperCase().contains(text.toUpperCase()) || i.getDescription().toUpperCase().contains(text.toUpperCase()))
                        && !text.isEmpty())
                .map(mapper::toResponse).collect(Collectors.toList());
    }
}
