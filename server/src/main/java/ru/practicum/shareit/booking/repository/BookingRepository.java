package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByBookerIdAndStatusIn(Long bookerId, List<Status> statuses, Pageable pageable);

    List<BookingEntity> findAllByBookerIdAndEndAfterAndStartBefore(Long booker, LocalDateTime end, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findAllByBookerIdAndStartAfter(Long booker, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findAllByBookerIdAndEndBefore(Long booker, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findAllByItemOwnerIdAndStatusIn(Long bookerId, List<Status> statuses, Pageable pageable);

    List<BookingEntity> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findAllByItemOwnerIdAndEndAfterAndStartBefore(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<BookingEntity> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findAllByItemIdAndStartBeforeAndStatusNotIn(Long itemId, LocalDateTime start, List<Status> statuses, Sort sort);

    List<BookingEntity> findAllByItemIdAndStartAfterAndStatusNotIn(Long itemId, LocalDateTime start, List<Status> statuses, Sort sort);

    List<BookingEntity> findAllByItemIdIn(Set<Long> itemId);

    //Получить все ранее арендованные вещи
    List<BookingEntity> findAllByItemIdAndBookerIdAndStatusNotInAndStartBefore(Long itemId, Long userId, List<Status> statuses, LocalDateTime start);
}
