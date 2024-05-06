package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.FromSizeRequest;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@DataJpaTest(properties = "db.name=test")
public class ItemRepositoryTests {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private ItemEntity itemEntity;
    private UserEntity testUser;

    private ItemRequestEntity itemRequest;
    private LocalDateTime created;
    private Long itemRequestId;
    private Long userId;
    private Long itemId;

    @BeforeEach
    public void setUp() {
        created = LocalDateTime.now();

        testUser = new UserEntity();
        testUser.setName("sdf");
        testUser.setEmail("dsgfd@sdf.ru");
        userId = userRepository.save(testUser).getId();

        itemRequest = new ItemRequestEntity();
        itemRequest.setDescription("Description");
        itemRequest.setCreated(created);
        itemRequest.setRequestor(testUser);

        itemRequestId = itemRequestRepository.save(itemRequest).getId();


        itemEntity = new ItemEntity();
        itemEntity.setName("Item 1");
        itemEntity.setDescription("Item description");
        itemEntity.setAvailable(true);
        itemEntity.setOwner(testUser);
        itemEntity.setRequest(itemRequest);

        itemId = itemRepository.save(itemEntity).getId();
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(testUser);
        itemRequestRepository.delete(itemRequest);
        itemRepository.delete(itemEntity);
    }

    @Test
    void createItem() {
        ItemEntity item = itemRepository.findById(itemId).orElseThrow(RuntimeException::new);

        assertTrue(item.getId().equals(itemId), "Id неверный");
        assertTrue(item.getDescription()
                .equals(itemEntity.getDescription()), "Имя неверное");
        assertTrue(item.getAvailable()
                .equals(itemEntity.getAvailable()), "Available неверный");
        assertTrue(item.getOwner().getId()
                .equals(userId), "Owner неверный");
        assertTrue(item.getRequest().getId()
                .equals(itemRequestId), "Request неверный");
    }

    @Test
    void findByIdAndOwnerIdItem() {
        ItemEntity item = itemRepository.findByIdAndOwnerId(itemId, userId).orElseThrow(RuntimeException::new);

        assertTrue(item.getId().equals(itemId), "Id неверный");
        assertTrue(item.getDescription()
                .equals(itemEntity.getDescription()), "Имя неверное");
        assertTrue(item.getAvailable()
                .equals(itemEntity.getAvailable()), "Available неверный");
        assertTrue(item.getOwner().getId()
                .equals(userId), "Owner неверный");
        assertTrue(item.getRequest().getId()
                .equals(itemRequestId), "Request неверный");
    }

    @Test
    void searchItem() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<ItemEntity> items = itemRepository.search("description", pageable);

        assertTrue(items.get(0).getId().equals(itemId), "Id неверный");
        assertTrue(items.get(0).getDescription()
                .equals(itemEntity.getDescription()), "Имя неверное");
        assertTrue(items.get(0).getAvailable()
                .equals(itemEntity.getAvailable()), "Available неверный");
        assertTrue(items.get(0).getOwner().getId()
                .equals(userId), "Owner неверный");
        assertTrue(items.get(0).getRequest().getId()
                .equals(itemRequestId), "Request неверный");
    }

    @Test
    void findAllByOwnerIdOrderByIdItem() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final Pageable pageable = FromSizeRequest.of(0, 5, sort);
        List<ItemEntity> items = itemRepository.findAllByOwnerIdOrderById(userId, pageable);

        assertTrue(items.get(0).getId().equals(itemId), "Id неверный");
        assertTrue(items.get(0).getDescription()
                .equals(itemEntity.getDescription()), "Имя неверное");
        assertTrue(items.get(0).getAvailable()
                .equals(itemEntity.getAvailable()), "Available неверный");
        assertTrue(items.get(0).getOwner().getId()
                .equals(userId), "Owner неверный");
        assertTrue(items.get(0).getRequest().getId()
                .equals(itemRequestId), "Request неверный");
    }

    @Test
    void findAllByRequestIdInItem() {
        List<ItemEntity> items = itemRepository.findAllByRequestIdIn(Collections.singleton(itemRequestId));

        assertTrue(items.get(0).getId().equals(itemId), "Id неверный");
        assertTrue(items.get(0).getDescription()
                .equals(itemEntity.getDescription()), "Имя неверное");
        assertTrue(items.get(0).getAvailable()
                .equals(itemEntity.getAvailable()), "Available неверный");
        assertTrue(items.get(0).getOwner().getId()
                .equals(userId), "Owner неверный");
        assertTrue(items.get(0).getRequest().getId()
                .equals(itemRequestId), "Request неверный");
    }
}
