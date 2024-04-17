package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse create(UserCreateRequest data);

    UserResponse update(Long userId, UserUpdateRequest data);

    List<UserResponse> getAll();

    UserResponse get(long id);

    void delete(long id);
}
