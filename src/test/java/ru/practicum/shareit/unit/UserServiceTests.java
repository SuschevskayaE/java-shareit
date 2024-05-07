package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.DuplicateException;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTests {
    private UserRepository repository;
    private UserRepositoryMapper repositoryMapper;

    private UserMapper mapper;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        repositoryMapper = Mappers.getMapper(UserRepositoryMapper.class);
        mapper = Mappers.getMapper(UserMapper.class);
        service = new UserServiceImpl(repository, mapper, repositoryMapper);
    }

    @Test
    void getAll() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findAll()).thenReturn(Collections.singletonList(user));
        var result = service.getAll();
        assertNotNull(result);
        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    void get() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        var result = service.get(user.getId());
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void update() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(any())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);

        UserUpdateRequest data = new UserUpdateRequest(user.getName(), user.getEmail());

        var result = service.update(user.getId(), data);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void updateNotFound() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(any())).thenThrow(new DataNotFoundException(String.format("Пользователь c id %s не найден", user.getId())));
        ;
        when(repository.save(any())).thenReturn(user);

        UserUpdateRequest data = new UserUpdateRequest(user.getName(), user.getEmail());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> service.update(user.getId(), data)
        );

        assertEquals(String.format("Пользователь c id %s не найден", user.getId()), exception.getMessage());
    }

    @Test
    void updateNotFoundById() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(any())).thenReturn(Optional.empty());
        ;
        when(repository.save(any())).thenReturn(user);

        UserUpdateRequest data = new UserUpdateRequest(user.getName(), user.getEmail());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> service.update(user.getId(), data)
        );

        assertEquals(String.format("Пользователь c id %s не найден", user.getId()), exception.getMessage());
    }

    @Test
    void updateDuplicateException() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(any())).thenReturn(Optional.of(user));
        ;
        when(repository.save(any())).thenReturn(user);
        when(repository.findAllByEmailAndIdNot(anyString(), anyLong())).thenReturn(Collections.singleton(user));

        UserUpdateRequest data = new UserUpdateRequest(user.getName(), user.getEmail());

        final DuplicateException exception = Assertions.assertThrows(
                DuplicateException.class,
                () -> service.update(user.getId(), data)
        );

        assertEquals(String.format("Email %s уже существует", user.getEmail()), exception.getMessage());
    }

    @Test
    void updateDuplicate() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Sasha");
        user.setEmail("sasha@mail.ru");

        when(repository.findById(any())).thenReturn(Optional.of(user));
        when(repository.findAllByEmailAndIdNot(any(), any())).thenThrow(new DuplicateException(String.format("Email %s уже существует", user.getEmail())));
        when(repository.save(any())).thenReturn(user);

        UserUpdateRequest data = new UserUpdateRequest(user.getName(), user.getEmail());

        final DuplicateException exception = Assertions.assertThrows(
                DuplicateException.class,
                () -> service.update(user.getId(), data)
        );

        assertEquals(String.format("Email %s уже существует", user.getEmail()), exception.getMessage());
    }
}
