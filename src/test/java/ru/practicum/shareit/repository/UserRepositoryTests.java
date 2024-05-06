package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@DataJpaTest(properties = "db.name=test")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private Long userId;

    @BeforeEach
    public void setUp() {
        testUser = new UserEntity();
        testUser.setName("sdf");
        testUser.setEmail("dsgfd@sdf.ru");
        userId = userRepository.save(testUser).getId();
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    void createUser() {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        assertNotNull(user);
        assertTrue(testUser.getId().equals(userId), "Id неверный");
        assertTrue(testUser.getName().equals(user.getName()), "Имя неверное");
        assertTrue(testUser.getEmail().equals(user.getEmail()), "Email неверный");
    }

    @Test
    void findAllUser() {
        List<UserEntity> users = userRepository.findAll();

        assertNotNull(users);
        assertTrue(users.size() == 1, "Размер отличается");
        assertTrue(testUser.getId().equals(users.get(0).getId()), "Id неверный");
        assertTrue(testUser.getName().equals(users.get(0).getName()), "Имя неверное");
        assertTrue(testUser.getEmail().equals(users.get(0).getEmail()), "Email неверный");
    }

    @Test
    void findAllByEmailAndIdNotUser() {
        Set<UserEntity> users = userRepository.findAllByEmailAndIdNot("dsgfd@sdf.ru", 5L);

        assertNotNull(users);
        assertTrue(users.size() == 1, "Размер отличается");
        assertTrue(testUser.getId().equals(users.stream().findFirst().orElseThrow().getId()), "Id неверный");
        assertTrue(testUser.getName().equals(users.stream().findFirst().orElseThrow().getName()), "Имя неверное");
        assertTrue(testUser.getEmail().equals(users.stream().findFirst().orElseThrow().getEmail()), "Email неверный");
    }
}
