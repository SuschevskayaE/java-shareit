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
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTests {

    private final EntityManager em;
    private final BookingService service;

    private final UserService userService;
    private final ItemService itemService;

    private BookingCreateRequest bookingCreateRequest;
    private Long itemId;
    private Long userId;
    private Long bookerId;
    private Long bookingId;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    public void setUp() {
        start = LocalDateTime.now().plusMinutes(1);
        end = LocalDateTime.now().plusDays(1);

        UserCreateRequest userCreateRequest = new UserCreateRequest("Sasha", "Sasha@mail.ru");
        userId = userService.create(userCreateRequest).getId();

        UserCreateRequest bookerCreateRequest = new UserCreateRequest("Sasha22", "Sasha22@mail.ru");
        bookerId = userService.create(bookerCreateRequest).getId();

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Item", "Item 1", true, null);
        itemId = itemService.create(userId, itemCreateRequest).getId();

        bookingCreateRequest = new BookingCreateRequest(start, end, itemId);
        bookingId = service.create(bookerId, bookingCreateRequest).getId();
    }

    @Test
    void createBooking() {
        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.id = :id", BookingEntity.class);

        BookingEntity bookingEntity = query.setParameter("id", bookingId)
                .getSingleResult();

        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingCreateRequest.getItemId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingCreateRequest.getStart()));
    }

    @Test
    void approvedBooking() {
        BookingResponse bookingResponse = service.approved(userId, bookingId, true);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.id = :id", BookingEntity.class);

        BookingEntity bookingEntity = query.setParameter("id", bookingId)
                .getSingleResult();

        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingResponse.getId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingResponse.getStart()));
        assertThat(bookingEntity.getStatus(), equalTo(bookingResponse.getStatus()));
    }

    @Test
    void approvedFalseBooking() {
        BookingResponse bookingResponse = service.approved(userId, bookingId, false);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.id = :id", BookingEntity.class);

        BookingEntity bookingEntity = query.setParameter("id", bookingId)
                .getSingleResult();

        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingResponse.getId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingResponse.getStart()));
        assertThat(bookingEntity.getStatus(), equalTo(bookingResponse.getStatus()));
    }

    @Test
    void getBooking() {
        BookingResponse bookingResponse = service.get(userId, bookingId);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.id = :id", BookingEntity.class);

        BookingEntity bookingEntity = query.setParameter("id", bookingId)
                .getSingleResult();

        assertThat(bookingEntity.getId(), notNullValue());
        assertThat(bookingEntity.getItem().getId(), equalTo(bookingResponse.getId()));
        assertThat(bookingEntity.getStart(), equalTo(bookingResponse.getStart()));
        assertThat(bookingEntity.getStatus(), equalTo(bookingResponse.getStatus()));
    }

    @Test
    void getUserAllBooking() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.ALL, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("status", Arrays.asList(Status.values())).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
        assertThat(bookingEntity.get(0).getStatus(), equalTo(bookingResponses.get(0).getStatus()));

    }

    @Test
    void getUserAllBookingWAITING() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.WAITING, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("status", Arrays.asList(Status.WAITING)).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
        assertThat(bookingEntity.get(0).getStatus(), equalTo(bookingResponses.get(0).getStatus()));
    }

    @Test
    void getUserAllBookingREJECTED() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.REJECTED, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("status", Arrays.asList(Status.REJECTED)).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getUserAllBookingCURRENT() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.CURRENT, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.end < :date and start > : date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getUserAllBookingPAST() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.PAST, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.end < : date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getUserAllBookingFUTURE() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getUserAll(bookerId, State.FUTURE, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.booker.id = :id and i.start > : date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", bookerId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getOwnerAllBookingWAITING() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.WAITING, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = : id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("status", Arrays.asList(Status.WAITING)).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
        assertThat(bookingEntity.get(0).getStatus(), equalTo(bookingResponses.get(0).getStatus()));
    }

    @Test
    void getOwnerAllBookingREJECTED() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.REJECTED, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = : id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("status", Arrays.asList(Status.REJECTED)).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getOwnerAllBookingPAST() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.PAST, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = :id and i.end < :date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getOwnerAllBookingCURRENT() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.CURRENT, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = :id and i.end < :date and start > : date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getOwnerAllBookingFUTURE() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.FUTURE, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = :id and start > : date", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("date", LocalDateTime.now()).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
    }

    @Test
    void getOwnerAllBooking() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<BookingResponse> bookingResponses = service.getOwnerAll(userId, State.ALL, pageable);

        TypedQuery<BookingEntity> query = em.createQuery("Select i from BookingEntity i where i.item.owner.id = : id and i.status in :status", BookingEntity.class);

        List<BookingEntity> bookingEntity = query
                .setParameter("id", userId)
                .setParameter("status", Arrays.asList(Status.values())).getResultList();

        assertThat(bookingEntity.size(), equalTo(bookingResponses.size()));
        assertThat(bookingEntity.get(0).getStatus(), equalTo(bookingResponses.get(0).getStatus()));
    }

}
