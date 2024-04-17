package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;

import java.util.List;

public interface ItemService {
    ItemResponse create(Long userId, ItemCreateRequest data);

    ItemResponse update(Long userId, Long itemId, ItemUpdateRequest data);

    List<ItemResponse> getAll(String text);

    List<ItemResponse> getUsersAll(Long userId);

    ItemResponse get(long id);
}
