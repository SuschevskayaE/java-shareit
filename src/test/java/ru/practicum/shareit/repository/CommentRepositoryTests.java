package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@DataJpaTest(properties = "db.name=test")
public class CommentRepositoryTests {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private ItemEntity itemEntity;
    private UserEntity userEntity;

    private CommentEntity comment;
    private LocalDateTime created;
    private Long commentId;
    private Long userId;
    private Long itemId;

    @BeforeEach
    public void setUp() {
        created = LocalDateTime.now();

        userEntity = new UserEntity();
        userEntity.setName("sdf");
        userEntity.setEmail("dsgfd@sdf.ru");
        userId = userRepository.save(userEntity).getId();

        itemEntity = new ItemEntity();
        itemEntity.setName("Item 1");
        itemEntity.setDescription("Item description");
        itemEntity.setAvailable(true);
        itemEntity.setOwner(userEntity);
        itemEntity.setRequest(null);
        itemId = itemRepository.save(itemEntity).getId();


        comment = new CommentEntity();
        comment.setText("text supper");
        comment.setCreated(created);
        comment.setItem(itemEntity);
        comment.setAuthor(userEntity);

        commentId = commentRepository.save(comment).getId();

    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(userEntity);
        itemRepository.delete(itemEntity);
        commentRepository.delete(comment);
    }

    @Test
    void createComment() {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

        assertTrue(comment.getId().equals(commentId), "Id неверный");
        assertTrue(comment.getText()
                .equals(commentEntity.getText()), "Text неверный");
        assertTrue(comment.getCreated()
                .equals(commentEntity.getCreated()), "Дата неверная");
        assertTrue(commentEntity.getItem().getId()
                .equals(itemId), "Item неверный");
        assertTrue(commentEntity.getAuthor().getId()
                .equals(userId), "Author неверный");
    }

    @Test
    void findAllByItemIdComment() {
        List<CommentEntity> commentEntities = commentRepository.findAllByItemId(itemId);

        assertTrue(comment.getId().equals(commentId), "Id неверный");
        assertTrue(comment.getText()
                .equals(commentEntities.get(0).getText()), "Text неверный");
        assertTrue(comment.getCreated()
                .equals(commentEntities.get(0).getCreated()), "Дата неверная");
        assertTrue(commentEntities.get(0).getItem().getId()
                .equals(itemId), "Item неверный");
        assertTrue(commentEntities.get(0).getAuthor().getId()
                .equals(userId), "Author неверный");
    }

    @Test
    void findAllByItemIdInComment() {
        List<CommentEntity> commentEntities = commentRepository.findAllByItemIdIn(Collections.singleton(itemId));

        assertTrue(comment.getId().equals(commentId), "Id неверный");
        assertTrue(comment.getText()
                .equals(commentEntities.get(0).getText()), "Text неверный");
        assertTrue(comment.getCreated()
                .equals(commentEntities.get(0).getCreated()), "Дата неверная");
        assertTrue(commentEntities.get(0).getItem().getId()
                .equals(itemId), "Item неверный");
        assertTrue(commentEntities.get(0).getAuthor().getId()
                .equals(userId), "Author неверный");
    }

}
