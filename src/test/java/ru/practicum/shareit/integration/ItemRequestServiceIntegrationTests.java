package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTests {

    private final EntityManager em;
    private final ItemRequestService service;

    private final UserService userService;
    private final ItemService itemService;
    private ItemRequestCreateRequest itemRequestCreateRequest;
    private Long itemRequestId;
    private Long userId;
    private ItemCreateRequest itemCreateRequest;
    private Long itemId;

    @BeforeEach
    public void setUp() {

        UserCreateRequest userCreateRequest = new UserCreateRequest("Sasha", "Sasha@mail.ru");
        userId = userService.create(userCreateRequest).getId();

        itemRequestCreateRequest = new ItemRequestCreateRequest("text");
        itemRequestId = service.create(userId, itemRequestCreateRequest).getId();

        itemCreateRequest = new ItemCreateRequest("Item", "Item 1", true, itemRequestId);
        itemId = itemService.create(userId, itemCreateRequest).getId();
    }

    @Test
    void createItemRequest() {
        TypedQuery<ItemRequestEntity> query = em.createQuery("Select i from ItemRequestEntity i where i.id = :id", ItemRequestEntity.class);

        ItemRequestEntity itemEntity = query.setParameter("id", itemRequestId)
                .getSingleResult();

        assertThat(itemEntity.getId(), notNullValue());
        assertThat(itemEntity.getDescription(), equalTo(itemRequestCreateRequest.getDescription()));
    }

    @Test
    void getItemRequest() {
        ItemRequestResponse itemRequestResponse = service.get(userId, itemRequestId);

        TypedQuery<ItemRequestEntity> query = em.createQuery("Select i from ItemRequestEntity i where i.id = :id", ItemRequestEntity.class);

        ItemRequestEntity itemRequestEntity = query.setParameter("id", itemRequestId)
                .getSingleResult();

        assertThat(itemRequestEntity.getId(), notNullValue());
        assertThat(itemRequestEntity.getDescription(), equalTo(itemRequestResponse.getDescription()));
    }

    @Test
    void getUsersAllItemRequest() {

        List<ItemRequestResponse> itemRequestResponses = service.getUsersAll(userId);

        TypedQuery<ItemRequestEntity> query = em.createQuery("Select i from ItemRequestEntity i where i.requestor.id = :id", ItemRequestEntity.class);

        List<ItemRequestEntity> itemEntity = query.setParameter("id", userId).getResultList();

        assertThat(itemEntity.size(), equalTo(itemRequestResponses.size()));
        assertThat(itemEntity.get(0).getDescription(), equalTo(itemRequestResponses.get(0).getDescription()));
    }

    @Test
    void getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<ItemRequestResponse> itemRequestResponses = service.getAll(55L, pageable);

        TypedQuery<ItemRequestEntity> query = em.createQuery("Select i from ItemRequestEntity i where i.requestor.id = :id", ItemRequestEntity.class);

        List<ItemRequestEntity> itemEntity = query.setParameter("id", userId).getResultList();

        assertThat(itemEntity.size(), equalTo(itemRequestResponses.size()));
        assertThat(itemEntity.get(0).getDescription(), equalTo(itemRequestResponses.get(0).getDescription()));
    }
}
