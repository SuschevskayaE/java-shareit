package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    private final UserService userService;

    @Override
    public Item create(Long userId, Item data) {
        userService.getAll().stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь с id %s не найден", userId)));

        data.setOwner(userId);
        return storage.create(userId, data);
    }

    @Override
    public Item update(Long userId, Item data) {
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
        return storage.update(userId, item);
    }

    @Override
    public List<Item> getAll() {
        return storage.getAll();
    }

    @Override
    public Item get(long id) {
        return storage.get(id);
    }
}
