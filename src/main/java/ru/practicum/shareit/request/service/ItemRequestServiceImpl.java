package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestRepositoryMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestMapper mapper;
    private final UserService userService;
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepositoryMapper repositoryMapper;
    private final ItemRepositoryMapper itemRepositoryMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestResponse create(Long userId, ItemRequestCreateRequest request) {
        ItemRequest data = mapper.toItemRequest(request);
        data.setRequestor(userService.getUser(userId));
        data.setCreated(LocalDateTime.now());
        ItemRequest modified = repositoryMapper.toItemRequest(repository.save(repositoryMapper.toEntity(data)));
        return mapper.toResponse(modified);
    }

    @Override
    public List<ItemRequestResponse> getUsersAll(Long userId) {
        userService.getUser(userId);

        Map<Long, ItemRequestResponse> itemRequests = repository.findAllByRequestorIdOrderByCreated(userId)
                .stream()
                .map(repositoryMapper::toItemRequest)
                .map(mapper::toResponse)
                .collect(Collectors.toMap(ItemRequestResponse::getId, itemRequest -> itemRequest));
        return getItemRequestWithItem(itemRequests);
    }

    @Override
    public List<ItemRequestResponse> getAll(Long userId, Pageable pageable) {
        Map<Long, ItemRequestResponse> itemRequests = repository.getAll(userId, pageable)
                .stream()
                .map(repositoryMapper::toItemRequest)
                .map(mapper::toResponse)
                .collect(Collectors.toMap(ItemRequestResponse::getId, itemRequest -> itemRequest));
        return getItemRequestWithItem(itemRequests);
    }

    private List<ItemRequestResponse> getItemRequestWithItem(Map<Long, ItemRequestResponse> itemRequests) {
        Map<Long, ItemResponse> items = itemRepository.findAllByRequestIdIn(itemRequests.keySet())
                .stream()
                .map(i -> itemRepositoryMapper.toItem(i, i.getOwner().getId()))
                .map(itemMapper::toResponse)
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        return itemRequests.values().stream()
                .map(itemRequest -> {
                    itemRequest.setItems(items.values()
                            .stream()
                            .filter(i -> i.getRequestId().equals(itemRequest.getId()))
                            .collect(Collectors.toList()));
                    return itemRequest;
                }).collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse get(Long userId, Long itemRequestId) {
        userService.getUser(userId);

        ItemRequestResponse itemRequestResponse = mapper.toResponse(getItemRequest(itemRequestId));

        List<ItemResponse> item = itemRepository.findAllByRequestIdIn(new HashSet<>(Arrays.asList(itemRequestId)))
                .stream()
                .map(i -> itemRepositoryMapper.toItem(i, i.getOwner().getId()))
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
        itemRequestResponse.setItems(item);
        return itemRequestResponse;
    }

    @Override
    public ItemRequest getItemRequest(Long itemRequestId) {
        ItemRequestEntity itemRequestEntity = repository.findById(itemRequestId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Запрос с id %s не найден", itemRequestId)));
        return repositoryMapper.toItemRequest(itemRequestEntity);
    }
}
