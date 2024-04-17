package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    private final UserService userService;

    private final ItemMapper mapper;

    @Override
    public ItemResponse create(Long userId, ItemCreateRequest request) {
        Item data = mapper.toItem(request);
        userService.getAll().stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));

        data.setOwner(userId);
        Item modified = storage.create(userId, data);
        return mapper.toResponse(modified);
    }

    @Override
    public ItemResponse update(Long userId, Long itemId, ItemUpdateRequest request) {
        Item data = mapper.toItem(request);
        data.setId(itemId);

        Item item = storage.get(data.getId());
        if (!item.getOwner().equals(userId)) {
            throw new DataNotFoundException("userId не владелец вещи");
        }
        if (data.getName() != null) {
            item.setName(data.getName());
        }
        if (data.getDescription() != null) {
            item.setDescription(data.getDescription());
        }
        if (data.getAvailable() != null) {
            item.setAvailable(data.getAvailable());
        }
        Item modified = storage.update(userId, item);
        return mapper.toResponse(modified);
    }

    @Override
    public List<ItemResponse> getUsersAll(Long userId) {
        return storage.getAll().stream().filter(i -> i.getOwner().equals(userId)).map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> getAll(String text) {
        return storage.getAll().stream().filter(i -> i.getAvailable().equals(true))
                .filter(i -> (i.getName().toUpperCase().contains(text.toUpperCase()) || i.getDescription().toUpperCase().contains(text.toUpperCase()))
                        && !text.isEmpty())
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ItemResponse get(long id) {
        Item modified = storage.get(id);
        return mapper.toResponse(modified);
    }
}
