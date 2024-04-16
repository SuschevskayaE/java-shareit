package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    private final UserMapper mapper;

    @GetMapping
    public List<UserResponse> usersAll() {
        return service.getAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        User user = mapper.toUser(request);
        User modified = service.create(user);
        return mapper.toResponse(modified);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable long userId, @Valid @RequestBody UserUpdateRequest request) {
        User user = mapper.toUser(request);
        user.setId(userId);
        User modified = service.update(user);
        return mapper.toResponse(modified);
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable long userId) {
        User modified = service.get(userId);
        return mapper.toResponse(modified);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        service.delete(userId);
    }
}
