package ru.practicum.shareit.item.repository.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Long userId, Item data);

    Item update(Long userId, Item data);

    List<Item> getAll();

    Item get(long id);

    void delete(long id);
}
