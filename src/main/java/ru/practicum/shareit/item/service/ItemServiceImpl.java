package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ItemRepositoryMapper repositoryMapper;

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingRepositoryMapper bookingRepositoryMapper;
    private final BookingMapper bookingMapper;

    private final ItemMapper mapper;
    private final CommentMapper commentMapper;
    private final CommentRepositoryMapper commentRepositoryMapper;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponse create(Long userId, ItemCreateRequest request) {
        Item data = mapper.toItem(request);
        data.setOwner(userService.getUser(userId));
        Item modified = repositoryMapper.toItem(repository.save(repositoryMapper.toEntity(data, userId)), userId);
        return mapper.toResponse(modified);
    }

    @Override
    public ItemResponse update(Long userId, Long itemId, ItemUpdateRequest request) {
        Item data = mapper.toItem(request);
        data.setId(itemId);
        ItemEntity item = repository.findByIdAndOwnerId(itemId, userId).orElseThrow(() -> new DataNotFoundException(String.format("Вещь с id %s не найдена", itemId)));

        repositoryMapper.updateEntity(data, item);
        return mapper.toResponse(repositoryMapper.toItem(repository.save(item), userId));
    }

    @Override
    public List<ItemUserCommentsResponse> getUsersAll(Long userId) {

        return repository.findAllByOwnerIdOrderById(userId).stream()

                .map(i -> repositoryMapper.toItem(i, i.getOwner().getId()))
                .map(mapper::toUserCommentsResponse)
                .map(i -> {
                    i.setLastBooking(bookingRepository
                            .findAllByItemIdAndEndBeforeAndStatusNotIn(i.getId(),
                                    LocalDateTime.now(),
                                    List.of(Status.REJECTED),
                                    Sort.by("end"))
                            .stream()
                            .map(bookingRepositoryMapper::toBooking)
                            .map(bookingMapper::toItemResponse)
                            .findFirst().orElse(null));
                    i.setNextBooking(bookingRepository
                            .findAllByItemIdAndStartAfterAndStatusNotIn(i.getId(),
                                    LocalDateTime.now(),
                                    List.of(Status.REJECTED),
                                    Sort.by("start"))
                            .stream()
                            .map(bookingRepositoryMapper::toBooking)
                            .map(bookingMapper::toItemResponse)
                            .findFirst().orElse(null));
                    i.setComments(commentRepository
                            .findAllByItemId(i.getId())
                            .stream()
                            .map(commentRepositoryMapper::toComment)
                            .map(commentMapper::toResponse)
                            .collect(Collectors.toList()));
                    return i;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> getAll(String text) {

        return repository.search(text).stream()
                .map(i -> repositoryMapper.toItem(i, i.getOwner().getId()))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemUserCommentsResponse get(long id, long userId) {

        Item item = getItem(id);
        ItemUserCommentsResponse itemFull = mapper.toUserCommentsResponse(item);

        List<CommentResponse> commentEntities = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(commentRepositoryMapper::toComment)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());

        itemFull.setComments(commentEntities);

        if (item.getOwner().getId().equals(userId)) {
            itemFull.setLastBooking(bookingRepository
                    .findAllByItemIdAndStartBeforeAndStatusNotIn(itemFull.getId(),
                            LocalDateTime.now(),
                            List.of(Status.REJECTED),
                            Sort.by("end").descending())
                    .stream()
                    .map(bookingRepositoryMapper::toBooking)
                    .map(bookingMapper::toItemResponse)
                    .findFirst().orElse(null));
            itemFull.setNextBooking(bookingRepository
                    .findAllByItemIdAndStartAfterAndStatusNotIn(itemFull.getId(),
                            LocalDateTime.now(),
                            List.of(Status.REJECTED),
                            Sort.by("start"))
                    .stream()
                    .map(bookingRepositoryMapper::toBooking)
                    .map(bookingMapper::toItemResponse)
                    .findFirst().orElse(null));
        }
        return itemFull;
    }

    @Override
    public Item getItem(long id) {
        ItemEntity entity = repository.findById(id).orElseThrow(() -> new DataNotFoundException(String.format("Вещь с id %s не найдена", id)));
        return repositoryMapper.toItem(entity, entity.getOwner().getId());
    }

    @Override
    public CommentResponse createComment(Long userId, Long itemId, CommentCreateRequest request) {

        bookingRepository.findAllByItemIdAndBookerIdAndStatusNotInAndStartBefore(itemId, userId, List.of(Status.REJECTED, Status.WAITING, Status.CANCELED), LocalDateTime.now())
                .stream().findFirst().orElseThrow(() -> new ValidationException(String.format("Заказ на вещь с id %s не найден", itemId)));

        Comment comment = commentMapper.toComment(request);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(getItem(itemId));
        comment.setAuthor(userService.getUser(userId));
        return commentMapper.toResponse(commentRepositoryMapper.toComment(commentRepository.save(commentRepositoryMapper.toEntity(comment))));
    }
}
