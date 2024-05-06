package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@DataJpaTest(properties = "db.name=test")
public class ItemRequestRepositoryTests {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    private ItemRequestEntity itemRequestEntity;
    private UserEntity testUser;
    private LocalDateTime created;

    private Long itemRequestId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        created = LocalDateTime.now();

        testUser = new UserEntity();
        testUser.setName("sdf");
        testUser.setEmail("dsgfd@sdf.ru");
        userId = userRepository.save(testUser).getId();

        itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setDescription("Description");
        itemRequestEntity.setCreated(created);
        itemRequestEntity.setRequestor(testUser);

        itemRequestId = itemRequestRepository.save(itemRequestEntity).getId();
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(testUser);
        itemRequestRepository.delete(itemRequestEntity);
    }

    @Test
    void createItemRequest() {
        ItemRequestEntity itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow();

        assertNotNull(itemRequest);
        assertTrue(itemRequest.getId().equals(itemRequestId), "Id неверный");
        assertTrue(itemRequestEntity.getDescription()
                .equals(itemRequest.getDescription()), "Имя неверное");
        assertTrue(itemRequestEntity.getCreated()
                .equals(itemRequest.getCreated()), "Дата неверная");
        assertTrue(itemRequestEntity.getRequestor().getId()
                .equals(itemRequest.getRequestor().getId()), "Requestor неверный");
    }

    @Test
    void findAllByRequestorIdOrderByCreatedItemRequest() {
        List<ItemRequestEntity> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreated(userId);

        assertNotNull(itemRequests);
        assertTrue(itemRequests.stream().findFirst().orElseThrow().getId().equals(itemRequestId), "Id неверный");
        assertTrue(itemRequestEntity.getDescription()
                .equals(itemRequests.stream().findFirst().orElseThrow().getDescription()), "Имя неверное");
        assertTrue(itemRequestEntity.getCreated()
                .equals(itemRequests.stream().findFirst().orElseThrow().getCreated()), "Дата неверная");
        assertTrue(itemRequestEntity.getRequestor().getId()
                .equals(itemRequests.stream().findFirst().orElseThrow().getRequestor().getId()), "Requestor неверный");
    }

    @Test
    void getAllItemRequest() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);

        List<ItemRequestEntity> itemRequests = itemRequestRepository.getAll(5L, pageable);

        assertNotNull(itemRequests);
        assertTrue(itemRequests.stream().findFirst().orElseThrow().getId().equals(itemRequestId), "Id неверный");
        assertTrue(itemRequestEntity.getDescription()
                .equals(itemRequests.stream().findFirst().orElseThrow().getDescription()), "Имя неверное");
        assertTrue(itemRequestEntity.getCreated()
                .equals(itemRequests.stream().findFirst().orElseThrow().getCreated()), "Дата неверная");
        assertTrue(itemRequestEntity.getRequestor().getId()
                .equals(itemRequests.stream().findFirst().orElseThrow().getRequestor().getId()), "Requestor неверный");
    }

}
