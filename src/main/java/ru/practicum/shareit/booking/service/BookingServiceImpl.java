package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper mapper;
    private final BookingRepositoryMapper repositoryMapper;
    private final BookingRepository repository;

    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingResponse create(Long userId, BookingCreateRequest request) {
        Booking booking = mapper.toBooking(request);

        booking.setItem(itemService.getItem(booking.getItem().getId()));

        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataNotFoundException(String.format("Пользователь c id %s владелец вещи %s", userId, booking.getItem().getId()));
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException(String.format("Вещь с id %s недоступна для заказа", booking.getItem().getId()));
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата end раньше даты start");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Дата end равна дате start");
        }

        booking.setBooker(userService.getUser(userId));
        booking.setStatus(Status.WAITING);
        BookingEntity entity = repository.save(repositoryMapper.toEntity(booking));
        return mapper.toResponse(repositoryMapper.toBooking(entity));
    }

    @Override
    public BookingResponse approved(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = repositoryMapper
                .toBooking(repository
                        .findById(bookingId)
                        .orElseThrow(() -> new DataNotFoundException(String.format("Заказ с id %s не найден", bookingId))));

        if (booking.getBooker().getId().equals(ownerId)) {
            throw new DataNotFoundException("Вы не владелец вещи");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException(String.format("Вещь с id %s не найдена", bookingId));
        }
        if (booking.getStatus().equals(Status.WAITING)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new ValidationException(String.format("Статус содержит значение %s", booking.getStatus()));
        }
        return mapper.toResponse(repositoryMapper
                .toBooking(repository
                        .save(repositoryMapper.toEntity(booking))));
    }

    @Override
    public BookingResponse get(long userId, long id) {
        Booking booking = repositoryMapper.toBooking(repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Заказ с id %s не найден", id))));

        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new DataNotFoundException(String.format("Заказ с id %s не найден", id));
        }
        return mapper.toResponse(booking);
    }


    @Override
    public List<BookingResponse> getUserAll(Long userId, State state, Pageable pageable) {
        List<BookingEntity> entities = new ArrayList<>();
        List<Status> statuses;
        userService.get(userId);

        switch (state) {
            case ALL:
                statuses = Arrays.asList(Status.values());
                entities = repository.findAllByBookerIdAndStatusIn(userId, statuses, pageable);
                break;
            case WAITING:
                statuses = List.of(Status.WAITING);
                entities = repository.findAllByBookerIdAndStatusIn(userId, statuses, pageable);
                break;
            case REJECTED:
                statuses = List.of(Status.REJECTED);
                entities = repository.findAllByBookerIdAndStatusIn(userId, statuses, pageable);
                break;
            case CURRENT:
                entities = repository.findAllByBookerIdAndEndAfterAndStartBefore(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                entities = repository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                entities = repository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                break;

        }
        return entities.stream()
                .map(repositoryMapper::toBooking)
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getOwnerAll(Long ownerId, State state, Pageable pageable) {
        List<BookingEntity> entities = new ArrayList<>();
        List<Status> statuses;
        userService.get(ownerId);

        switch (state) {
            case ALL:
                statuses = Arrays.asList(Status.values());
                entities = repository.findAllByItemOwnerIdAndStatusIn(ownerId, statuses, pageable);
                break;
            case WAITING:
                statuses = Arrays.asList(Status.WAITING);
                entities = repository.findAllByItemOwnerIdAndStatusIn(ownerId, statuses, pageable);
                break;
            case REJECTED:
                statuses = Arrays.asList(Status.REJECTED);
                entities = repository.findAllByItemOwnerIdAndStatusIn(ownerId, statuses, pageable);
                break;
            case CURRENT:
                entities = repository.findAllByItemOwnerIdAndEndAfterAndStartBefore(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                entities = repository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                entities = repository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), pageable);
                break;

        }
        return entities.stream()
                .map(repositoryMapper::toBooking)
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
