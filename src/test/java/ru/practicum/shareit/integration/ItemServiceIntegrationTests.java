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
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    private final ItemRequestService itemRequestService;

    private ItemCreateRequest itemCreateRequest;
    private Long itemId;
    private Long userId;
    private Long bookerId;
    private Long bookingId;
    private Long requestId;

    private Long commentId;

    @BeforeEach
    public void setUp() {
        UserCreateRequest userCreateRequest = new UserCreateRequest("Sasha", "Sasha@mail.ru");
        userId = userService.create(userCreateRequest).getId();

        itemCreateRequest = new ItemCreateRequest("Item", "Item 1", true, requestId);
        itemId = service.create(userId, itemCreateRequest).getId();

        ItemRequestCreateRequest data = new ItemRequestCreateRequest("Дай мне сил..");
        requestId = itemRequestService.create(userId, data).getId();

        UserCreateRequest bookerCreateRequest = new UserCreateRequest("Sasha22", "Sasha22@mail.ru");
        bookerId = userService.create(bookerCreateRequest).getId();

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(LocalDateTime.now().plusNanos(2), LocalDateTime.now().plusMinutes(3), itemId);
        bookingId = bookingService.create(bookerId, bookingCreateRequest).getId();
        bookingService.approved(userId, bookingId, true);

        BookingCreateRequest bookingCreateRequestRejected = new BookingCreateRequest(LocalDateTime.now().plusNanos(2), LocalDateTime.now().plusMinutes(3), itemId);
        Long bookingIdRejected = bookingService.create(bookerId, bookingCreateRequest).getId();
        bookingService.approved(userId, bookingIdRejected, false);

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("text");
        commentId = service.createComment(bookerId, itemId, commentCreateRequest).getId();
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
    void createItemRequestNull() {
        itemCreateRequest = new ItemCreateRequest("Item", "Item 1", true, null);
        itemId = service.create(userId, itemCreateRequest).getId();

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemCreateRequest.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemCreateRequest.getDescription()));
        assertThat(itemEntity.getRequest(), nullValue());
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
    void getUsersAllWithBookings() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Item 3", "Item 3", true, null);
        itemId = service.create(userId, itemCreateRequest).getId();

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(start, end, itemId);
        bookingId = bookingService.create(bookerId, bookingCreateRequest).getId();

        LocalDateTime startFinish = LocalDateTime.now().minusDays(1);
        LocalDateTime endFinish = LocalDateTime.now().minusMinutes(20);

        BookingCreateRequest bookingCreateRequestFinish = new BookingCreateRequest(startFinish, endFinish, itemId);
        bookingService.create(bookerId, bookingCreateRequestFinish);

        List<ItemUserCommentsResponse> users = service.getUsersAll(userId, pageable);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.owner.id = :id", ItemEntity.class);

        List<ItemEntity> itemEntity = query.setParameter("id", userId).getResultList();

        assertThat(itemEntity.size(), equalTo(users.size()));
        assertThat(itemEntity.get(0).getName(), equalTo(users.get(0).getName()));
    }

    @Test
    void getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);

        List<ItemResponse> users = service.getAll("It", pageable);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where upper(i.name) like upper(concat('%', :text, '%')) " +
                "or upper(i.description) like upper(concat('%', :text, '%')) " +
                "or :text is null ", ItemEntity.class);

        List<ItemEntity> itemEntity = query.setParameter("text", "It").getResultList();

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
    void getItemOwner() {
        ItemUserCommentsResponse itemUserCommentsResponse = service.get(itemId, bookerId);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id and i.owner.id != :owner_id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .setParameter("owner_id", bookerId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemUserCommentsResponse.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUserCommentsResponse.getDescription()));
    }

    @Test
    void createComment() {
        UserCreateRequest userCreateRequestTwo = new UserCreateRequest("Sasha225", "Sasha225@mail.ru");
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

    @Test
    void getComment() {
        ItemUserCommentsResponse itemUserCommentsResponse = service.get(itemId, userId);

        TypedQuery<CommentEntity> query = em.createQuery("Select c from CommentEntity c where c.id = :id ", CommentEntity.class);

        CommentEntity commentEntity = query.setParameter("id", commentId)
                .getSingleResult();

        assertThat(commentEntity.getId(), notNullValue());
        assertThat(commentEntity.getText(), equalTo(itemUserCommentsResponse.getComments().get(0).getText()));
        assertThat(commentEntity.getId(), equalTo(itemUserCommentsResponse.getComments().get(0).getId()));
        assertThat(commentEntity.getAuthor().getName(), equalTo(itemUserCommentsResponse.getComments().get(0).getAuthorName()));
    }

    @Test
    void getItemWithBookings() {
        ItemUserCommentsResponse itemUserCommentsResponse = service.get(itemId, userId);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id and i.owner.id = :owner_id", ItemEntity.class);

        ItemEntity itemEntity = query.setParameter("id", itemId)
                .setParameter("owner_id", userId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getName(), equalTo(itemUserCommentsResponse.getName()));
        assertThat(itemEntity.getDescription(), equalTo(itemUserCommentsResponse.getDescription()));
    }
}
