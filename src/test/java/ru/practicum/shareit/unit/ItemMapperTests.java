package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTests {

    private ItemMapper itemMapper;
    private ItemRepositoryMapper itemRepositoryMapper;

    @Mock
    private UserRepositoryMapper userRepositoryMapper;

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
