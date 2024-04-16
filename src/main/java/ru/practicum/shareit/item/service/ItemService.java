package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Long userId, Item data);

    Item update(Long userId, Item data);

    List<Item> getAll();

    Item get(long id);
}
