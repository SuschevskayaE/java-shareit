package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {


    private final Map<Long, Item> items = new HashMap<>();

    private long id;

    @Override
    public Item create(Long userId, Item data) {
        if (data.getId() == null) {
            data.setId(++id);
        } else {
            throw new ValidationException("Id должно быть null");
        }
        items.put(data.getId(), data);
        return data;
    }

    @Override
    public Item update(Long userId, Item data) {
        if (!items.containsKey(data.getId())) {
            throw new DataNotFoundException(String.format("Элемент %s не найден", data));
        }
        items.put(data.getId(), data);
        return data;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item get(long id) {
        if (!items.containsKey(id)) {
            throw new DataNotFoundException(String.format("Элемент с id %s не найден", id));
        }
        return items.get(id);
    }

    @Override
    public void delete(long id) {
        if (!items.containsKey(id)) {
            throw new DataNotFoundException(String.format("Элемент с id %s не найден", id));
        }
        items.remove(id);
    }
}
