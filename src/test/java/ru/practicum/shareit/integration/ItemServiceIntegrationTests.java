package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.item.controller.dto.CommentCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.controller.dto.ItemUserCommentsResponse;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTests {

    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;

    private ItemCreateRequest itemCreateRequest;
    private Long itemId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        UserCreateRequest userCreateRequest = new UserCreateRequest("Sasha", "Sasha@mail.ru");
        userId = userService.create(userCreateRequest).getId();

        itemCreateRequest = new ItemCreateRequest("Item", "Item 1", true, null);
        itemId = service.create(userId, itemCreateRequest).getId();
    }

    @Test
    void createItem() {
        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemCreateRequest.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemCreateRequest.getDescription()));
    }

    @Test
    void updateItem() {
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest("Item 2", "Item 2", false, null);
        service.update(userId, itemId, itemUpdateRequest);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemUpdateRequest.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUpdateRequest.getDescription()));
    }

    @Test
    void getUsersAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);

        List<ItemUserCommentsResponse> users = service.getUsersAll(userId, pageable);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.owner.id = :id", ItemEntity.class);

        List<ItemEntity> itemEntity = query.setParameter("id", userId).getResultList();

        assertThat(itemEntity.size(), equalTo(users.size()));
        assertThat(itemEntity.get(0).getName(), equalTo(users.get(0).getName()));
    }

    @Test
    void getItem() {
        ItemUserCommentsResponse itemUserCommentsResponse = service.get(itemId, userId);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id and i.owner.id = :owner_id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .setParameter("owner_id", userId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemUserCommentsResponse.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUserCommentsResponse.getDescription()));
    }

    @Test
    void createComment() {
        UserCreateRequest userCreateRequestTwo = new UserCreateRequest("Sasha22", "Sasha22@mail.ru");
        Long userIdTwo = userService.create(userCreateRequestTwo).getId();

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now().plusNanos(2), LocalDateTime.now().plusMinutes(3), itemId);
        Long bookingId = bookingService.create(userIdTwo, bookingCreateRequest).getId();
        bookingService.approved(userId, bookingId, true);

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("text");
        Long commentId = service.createComment(userIdTwo, itemId, commentCreateRequest).getId();

        TypedQuery<CommentEntity> query = em.createQuery("Select c from CommentEntity c where c.id = :id ", CommentEntity.class);

        CommentEntity commentEntity = query.setParameter("id", commentId)
                .getSingleResult();

        assertThat(commentEntity.getId(), notNullValue());
        assertThat(commentEntity.getText(), equalTo(commentCreateRequest.getText()));
        assertThat(commentEntity.getItem().getId(), equalTo(itemId));
        assertThat(commentEntity.getAuthor().getId(), equalTo(userIdTwo));
    }
}
