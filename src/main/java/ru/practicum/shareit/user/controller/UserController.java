package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable long userId, @Valid @RequestBody UserUpdateRequest request) {
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
