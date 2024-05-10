package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestRepositoryMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepositoryMapper repositoryMapper;
    @Mock
    private ItemRepositoryMapper itemRepositoryMapper;
    @Mock
    private ItemMapper itemMapper;

    @Test
    void getItemRequestNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getItemRequest(100L)
        );

        assertEquals(String.format(String.format("Запрос с id %s не найден", 100L)), exception.getMessage());
    }
}
