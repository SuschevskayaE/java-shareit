package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTests {

    private final EntityManager em;
    private final UserService service;

    private UserCreateRequest userCreateRequest;
    private Long userId;

    @BeforeEach
    public void setUp() {
        userCreateRequest = new UserCreateRequest("Sasha", "Sasha@mail.ru");
        userId = service.create(userCreateRequest).getId();
    }

    @AfterEach
    public void tearDown() {
        service.delete(userId);
    }

    @Test
    void createUser() {
        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.name = :name", UserEntity.class);

        UserEntity userEntity = query.setParameter("name", userCreateRequest.getName())
                .getSingleResult();

        assertThat(userEntity.getId(), notNullValue());
        assertThat(userEntity.getName(), equalTo(userCreateRequest.getName()));
        assertThat(userEntity.getEmail(), equalTo(userCreateRequest.getEmail()));
    }

    @Test
    void updateUser() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Sasha 2", "Sasha2@mail.ru");
        service.update(userId, userUpdateRequest);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.name = :name", UserEntity.class);

        UserEntity userEntity = query.setParameter("name", userUpdateRequest.getName())
                .getSingleResult();

        assertThat(userEntity.getId(), notNullValue());
        assertThat(userEntity.getName(), equalTo(userUpdateRequest.getName()));
        assertThat(userEntity.getEmail(), equalTo(userUpdateRequest.getEmail()));
    }

    @Test
    void getUser() {
        UserResponse userResponse = service.get(userId);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.id = :id", UserEntity.class);

        UserEntity userEntity = query.setParameter("id", userId)
                .getSingleResult();

        assertThat(userEntity.getId(), notNullValue());
        assertThat(userEntity.getName(), equalTo(userResponse.getName()));
        assertThat(userEntity.getEmail(), equalTo(userResponse.getEmail()));
    }
}
