package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTests {

    private ItemMapper itemMapper;
    private ItemRepositoryMapper itemRepositoryMapper;

    @BeforeEach
    void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
        itemRepositoryMapper = Mappers.getMapper(ItemRepositoryMapper.class);
    }

    @Test
    void toItem() {
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Name", "Des", true, null);
        Item item = itemMapper.toItem(itemCreateRequest);

        assertEquals(item.getName(), itemCreateRequest.getName());
        assertEquals(item.getDescription(), itemCreateRequest.getDescription());
    }

    @Test
    void toItemNull() {
        Item item = itemRepositoryMapper.toItem(null, null);
        assertNull(item);
    }

    @Test
    void toEntityNull() {
        ItemEntity item = itemRepositoryMapper.toEntity(null, null);
        assertNull(item);
    }
}
