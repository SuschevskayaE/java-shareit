package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public User create(User data) {

        storage.getAll()
                .stream()
                .filter(d -> d.getEmail().equals(data.getEmail()))
                .findFirst()
                .ifPresent(
                        (x) -> {
                            throw new ValidationException(String.format("Email %s уже существует", x.getEmail()));
                        }
                );
        return storage.create(data);
    }

    @Override
    public User update(User data) {
        User user = storage.get(data.getId());

        storage.getAll().stream().filter(d -> d.getEmail().equals(data.getEmail()))
                .filter(d -> d.getId() != data.getId())
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
        return storage.update(user);
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    @Override
    public User get(long id) {
        return storage.get(id);
    }

    @Override
    public void delete(long id) {
        storage.delete(id);
    }
}
