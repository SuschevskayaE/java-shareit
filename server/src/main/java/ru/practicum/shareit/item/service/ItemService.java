package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemResponse create(Long userId, ItemCreateRequest data);

    ItemResponse update(Long userId, Long itemId, ItemUpdateRequest data);

    List<ItemResponse> getAll(String text, Pageable pageable);

    List<ItemUserCommentsResponse> getUsersAll(Long userId, Pageable pageable);

    ItemUserCommentsResponse get(long id, long userId);

    Item getItem(long id);

    CommentResponse createComment(Long userId, Long itemId, CommentCreateRequest request);
}
