package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private final UserMapper mapper;

    @Override
    public UserResponse create(UserCreateRequest request) {

        User data = mapper.toUser(request);
        storage.getAll()
                .stream()
                .filter(d -> d.getEmail().equals(data.getEmail()))
                .findFirst()
                .ifPresent(
                        (x) -> {
                            throw new ValidationException(String.format("Email %s уже существует", x.getEmail()));
                        }
                );
        User modified = storage.create(data);
        return mapper.toResponse(modified);
    }

    @Override
    public UserResponse update(Long userId, UserUpdateRequest request) {

        User data = mapper.toUser(request);
        data.setId(userId);

        User user = storage.get(data.getId());

        storage.getAll().stream().filter(d -> d.getEmail().equals(data.getEmail()))
                .filter(d -> !d.getId().equals(data.getId()))
                .findFirst()
                .ifPresent(
                        (x) -> {
                            throw new javax.validation.ValidationException(String.format("Email %s уже существует", x.getEmail()));
                        }
                );

        if (data.getName() != null) {
            user.setName(data.getName());
        }
        if (data.getEmail() != null) {
            user.setEmail(data.getEmail());
        }
        User modified = storage.update(user);
        return mapper.toResponse(modified);
    }

    @Override
    public List<UserResponse> getAll() {
        return storage.getAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse get(long id) {
        User modified = storage.get(id);
        return mapper.toResponse(modified);
    }

    @Override
    public void delete(long id) {
        storage.delete(id);
    }
}
