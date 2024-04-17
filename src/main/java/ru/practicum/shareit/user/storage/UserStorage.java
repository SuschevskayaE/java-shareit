package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User data);

    User update(User data);

    List<User> getAll();

    User get(long id);

    void delete(long id);
}
