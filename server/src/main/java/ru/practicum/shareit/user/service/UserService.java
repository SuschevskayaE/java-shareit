package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponse create(UserCreateRequest data);

    UserResponse update(Long userId, UserUpdateRequest data);

    List<UserResponse> getAll();

    UserResponse get(long id);

    User getUser(long id);

    void delete(long id);
}
