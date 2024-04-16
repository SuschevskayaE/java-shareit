package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User create(User data);

    User update(User data);

    List<User> getAll();

    User get(long id);

    void delete(long id);
}
