package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.controller.dto.CommentCreateRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository repository;

    @Mock
    private ItemRepositoryMapper repositoryMapper;

    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingRepositoryMapper bookingRepositoryMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper mapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepositoryMapper commentRepositoryMapper;
    @Mock
    private CommentRepository commentRepository;


    @Test
    void getItemDataNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.getItem(100L)
        );

        assertEquals(String.format(String.format("Вещь с id %s не найдена", 100L)), exception.getMessage());
    }

    @Test
    void createCommentValidation() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);

        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusNotInAndStartBefore(anyLong(),anyLong(),any(), any()))
                .thenReturn(Collections.emptyList());

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.createComment(user.getId(),item.getId(),commentCreateRequest)
        );

        assertEquals(String.format(String.format("Заказ на вещь с id %s не найден", item.getId())), exception.getMessage());
    }
}
