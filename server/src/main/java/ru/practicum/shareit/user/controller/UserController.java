package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserResponse> usersAll() {
        return service.getAll();
    }

    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable long userId, @RequestBody UserUpdateRequest request) {
        return service.update(userId, request);
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable long userId) {
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        service.delete(userId);
    }
}
