package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.DuplicateException;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;
    private final UserRepositoryMapper mapperRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {

        User data = mapper.toUser(request);
        UserEntity entity = repository.save(mapperRepository.toEntity(data));
        User modified = mapperRepository.toUser(entity);
        return mapper.toResponse(modified);
    }

    @Override
    public UserResponse update(Long userId, UserUpdateRequest request) {

        User data = mapper.toUser(request);
        data.setId(userId);

        User user = mapperRepository.toUser(repository.findById(userId).orElseThrow(
                () -> new DataNotFoundException(String.format("Пользователь c id %s не найден", userId))));

        repository.findAllByEmailAndIdNot(data.getEmail(), userId)
                .stream()
                .findFirst()
                .ifPresent(
                        (x) -> {
                            throw new DuplicateException(String.format("Email %s уже существует", x.getEmail()));
                        }
                );

        UserEntity entity = mapperRepository.toEntity(user);
        mapperRepository.updateEntity(data, entity);

        return mapper.toResponse(mapperRepository.toUser(repository.save(entity)));
    }

    @Override
    public List<UserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapperRepository::toUser)
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse get(long id) {
        User user = repository.findById(id)
                .map(mapperRepository::toUser)
                .orElseThrow(
                        () -> new DataNotFoundException(String.format("Пользователь c id %s не найден", id))
                );
        return mapper.toResponse(user);
    }


    public User getUser(long id) {
        return repository.findById(id)
                .map(mapperRepository::toUser)
                .orElseThrow(
                        () -> new DataNotFoundException(String.format("Пользователь c id %s не найден", id))
                );
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
