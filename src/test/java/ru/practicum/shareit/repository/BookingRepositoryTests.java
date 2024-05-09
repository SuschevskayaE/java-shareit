package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@DataJpaTest(properties = "db.name=test")
public class BookingRepositoryTests {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private ItemEntity itemEntity;
    private UserEntity userEntity;

    private BookingEntity booking;
    private LocalDateTime start;
    private LocalDateTime end;

    private Long userId;
    private Long itemId;

    private Long bookingId;

    private Sort sort;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        sort = Sort.by(Sort.Direction.DESC, "start");
        pageable = FromSizeRequest.of(0, 5, sort);

        userEntity = new UserEntity();
        userEntity.setName("sdf");
        userEntity.setEmail("dsgfd@sdf.ru");
        userId = userRepository.save(userEntity).getId();

        itemEntity = new ItemEntity();
        itemEntity.setName("Item 1");
        itemEntity.setDescription("Item description");
        itemEntity.setAvailable(true);
        itemEntity.setOwner(userEntity);
        itemEntity.setRequest(null);
        itemId = itemRepository.save(itemEntity).getId();

        booking = new BookingEntity();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.WAITING);
        booking.setBooker(userEntity);
        booking.setItem(itemEntity);

        bookingId = bookingRepository.save(booking).getId();
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(userEntity);
        itemRepository.delete(itemEntity);
        bookingRepository.delete(booking);
    }

    @Test
    void createBooking() {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElseThrow(RuntimeException::new);

        assertTrue(bookingEntity.getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookingEntity.getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookingEntity.getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookingEntity.getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookingEntity.getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookingEntity.getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemIdAndBookerIdAndStatusNotInAndStartBeforeBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusNotInAndStartBefore(itemId, userId, Collections.singletonList(Status.REJECTED), start.plusMinutes(2));

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemIdInBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemIdIn(Collections.singleton(itemId));

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemIdAndStartAfterAndStatusNotInBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemIdAndStartAfterAndStatusNotIn(itemId, start.minusMinutes(2), Collections.singletonList(Status.REJECTED), sort);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemIdAndStartBeforeAndStatusNotInBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemIdAndStartBeforeAndStatusNotIn(itemId, start.plusMinutes(2), Collections.singletonList(Status.REJECTED), sort);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemOwnerIdAndStartAfter(userId, start.minusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemOwnerIdAndEndAfterAndStartBeforeBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemOwnerIdAndEndAfterAndStartBefore(userId, start.plusMinutes(2), end.minusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemOwnerIdAndEndBefore(userId, end.plusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByItemOwnerIdAndStatusInBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusIn(userId, Collections.singletonList(Status.WAITING), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByBookerIdAndEndBeforeBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByBookerIdAndEndBefore(userId, end.plusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByBookerIdAndStartAfterBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByBookerIdAndStartAfter(userId, start.minusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

    @Test
    void findAllByBookerIdAndEndAfterAndStartBeforeBooking() {
        List<BookingEntity> bookings = bookingRepository
                .findAllByBookerIdAndEndAfterAndStartBefore(userId, end.minusMinutes(2), start.plusMinutes(2), pageable);

        assertTrue(bookings.get(0).getId().equals(bookingId), "Id неверный");
        assertTrue(booking.getStart().equals(bookings.get(0).getStart()), "Дата неверная");
        assertTrue(booking.getEnd().equals(bookings.get(0).getEnd()), "Дата неверная");
        assertTrue(booking.getStatus().equals(bookings.get(0).getStatus()), "Статус неверный");
        assertTrue(booking.getBooker().getId().equals(bookings.get(0).getBooker().getId()), "Booker неверный");
        assertTrue(booking.getItem().getId().equals(bookings.get(0).getItem().getId()), "Item неверный");
    }

}
