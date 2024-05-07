package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    private BookingMapper mapper;
    @Mock
    private BookingRepositoryMapper repositoryMapper;
    @Mock
    private BookingRepository repository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    @Test
    void createDataNotFound() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);

        when(mapper.toBooking(any())).thenReturn(booking);
        when(itemService.getItem(anyLong())).thenReturn(item);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now(), LocalDateTime.now(), item.getId());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.create(user.getId(), bookingCreateRequest)
        );

        assertEquals(String.format("Пользователь c id %s владелец вещи %s", user.getId(), booking.getItem().getId()), exception.getMessage());
    }

    @Test
    void createAvailableFalse() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", false, user, null, null, null, null);

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);

        when(mapper.toBooking(any())).thenReturn(booking);
        when(itemService.getItem(anyLong())).thenReturn(item);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now(), LocalDateTime.now(), item.getId());

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booker.getId(), bookingCreateRequest)
        );
        assertEquals(String.format("Вещь с id %s недоступна для заказа", booking.getItem().getId()), exception.getMessage());
    }

    @Test
    void createEndWrong() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date.plusDays(2));
        booking.setEnd(date);

        when(mapper.toBooking(any())).thenReturn(booking);
        when(itemService.getItem(anyLong())).thenReturn(item);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now(), LocalDateTime.now(), item.getId());

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booker.getId(), bookingCreateRequest)
        );
        assertEquals("Дата end раньше даты start", exception.getMessage());
    }

    @Test
    void createEndAndStartEquals() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);

        when(mapper.toBooking(any())).thenReturn(booking);
        when(itemService.getItem(anyLong())).thenReturn(item);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now(), LocalDateTime.now(), item.getId());

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booker.getId(), bookingCreateRequest)
        );
        assertEquals("Дата end равна дате start", exception.getMessage());
    }

    @Test
    void approvedNotFoundBooking() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.approved(booker.getId(), booking.getId(), true)
        );
        assertEquals(String.format("Заказ с id %s не найден", booking.getId()), exception.getMessage());
    }

    @Test
    void approvedNotFoundOwner() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);

        var userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());

        var bookingEntity = new BookingEntity();
        bookingEntity.setBooker(userEntity);
        bookingEntity.setId(1L);
        bookingEntity.setStart(date);
        bookingEntity.setEnd(date);

        when(repositoryMapper.toBooking(any())).thenReturn(booking);
        when(repository.findById(any())).thenReturn(Optional.of(bookingEntity));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.approved(booker.getId(), booking.getId(), true)
        );
        assertEquals(String.format("Вы не владелец вещи", booking.getId()), exception.getMessage());
    }

    @Test
    void approvedNotFoundId() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);

        var userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());

        var bookingEntity = new BookingEntity();
        bookingEntity.setBooker(userEntity);
        bookingEntity.setId(1L);
        bookingEntity.setStart(date);
        bookingEntity.setEnd(date);

        when(repositoryMapper.toBooking(any())).thenReturn(booking);
        when(repository.findById(any())).thenReturn(Optional.of(bookingEntity));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approved(5L, booking.getId(), true)
        );
        assertEquals(String.format("Вещь с id %s не найдена", booking.getId()), exception.getMessage());
    }

    @Test
    void getNotFound() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);

        var userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());

        var bookingEntity = new BookingEntity();
        bookingEntity.setBooker(userEntity);
        bookingEntity.setId(1L);
        bookingEntity.setStart(date);
        bookingEntity.setEnd(date);

        when(repository.findById(any())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.get(user.getId(), booking.getId())
        );
        assertEquals(String.format("Заказ с id %s не найден", booking.getId()), exception.getMessage());
    }

    @Test
    void getNotFoundOwner() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);

        var userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());

        var bookingEntity = new BookingEntity();
        bookingEntity.setBooker(userEntity);
        bookingEntity.setId(1L);
        bookingEntity.setStart(date);
        bookingEntity.setEnd(date);

        when(repositoryMapper.toBooking(any())).thenReturn(booking);
        when(repository.findById(any())).thenReturn(Optional.of(bookingEntity));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.get(55L, booking.getId())
        );
        assertEquals(String.format("Заказ с id %s не найден", booking.getId()), exception.getMessage());
    }

    @Test
    void approvedStatusWrong() {
        var user = new User(1L, "Sasha", "sasha@mail.ru");
        var booker = new User(2L, "Sasha", "sasha@mail.ru");
        var item = new Item(1L, "Item", "Des", true, user, null, null, null, null);

        LocalDateTime date = LocalDateTime.now();

        var booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(date);
        booking.setEnd(date);
        booking.setBooker(booker);
        booking.setStatus(Status.REJECTED);

        var userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());

        var bookingEntity = new BookingEntity();
        bookingEntity.setBooker(userEntity);
        bookingEntity.setId(1L);
        bookingEntity.setStart(date);
        bookingEntity.setEnd(date);

        when(repositoryMapper.toBooking(any())).thenReturn(booking);
        when(repository.findById(any())).thenReturn(Optional.of(bookingEntity));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approved(user.getId(), booking.getId(), true)
        );
        assertEquals(String.format("Статус содержит значение %s", booking.getStatus()), exception.getMessage());
    }
}
