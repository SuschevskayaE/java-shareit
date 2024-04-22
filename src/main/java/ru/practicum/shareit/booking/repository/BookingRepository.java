package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, List<Status> statuses);

    List<BookingEntity> findAllByBookerIdAndEndAfterAndStartBefore(Long booker, LocalDateTime end, LocalDateTime start);

    List<BookingEntity> findAllByBookerIdAndStartAfterOrderByStartDesc(Long booker, LocalDateTime start);

    List<BookingEntity> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start);

    List<BookingEntity> findAllByItemOwnerIdAndStatusInOrderByStartDesc(Long bookerId, List<Status> statuses);

    List<BookingEntity> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<BookingEntity> findAllByItemOwnerIdAndEndAfterAndStartBefore(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<BookingEntity> findAllByItemIdAndStartBeforeAndStatusNotIn(Long itemId, LocalDateTime start, List<Status> statuses, Sort sort);

    List<BookingEntity> findAllByItemIdAndEndBeforeAndStatusNotIn(Long itemId, LocalDateTime end, List<Status> statuses, Sort sort);

    List<BookingEntity> findAllByItemIdAndStartAfterAndStatusNotIn(Long itemId, LocalDateTime start, List<Status> statuses, Sort sort);


    //Получить все ранее арендованные вещи
    List<BookingEntity> findAllByItemIdAndBookerIdAndStatusNotInAndStartBefore(Long itemId, Long userId, List<Status> statuses, LocalDateTime start);
}
