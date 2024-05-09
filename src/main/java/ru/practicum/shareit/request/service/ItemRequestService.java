package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse create(Long itemRequestId, ItemRequestCreateRequest data);

    List<ItemRequestResponse> getUsersAll(Long userId);

    List<ItemRequestResponse> getAll(Long userId, Pageable pageable);

    ItemRequestResponse get(Long userId, Long itemRequestId);

    ItemRequest getItemRequest(Long itemRequestId);
}
