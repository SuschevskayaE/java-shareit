package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private long id;

    @Override
    public User create(User data) {
        if (data.getId() == null) {
            data.setId(++id);
        } else {
            throw new ValidationException("Id должно быть null");
        }
        users.put(data.getId(), data);
        return data;
    }

    @Override
    public User update(User data) {
        if (!users.containsKey(data.getId())) {
            throw new DataNotFoundException(String.format("Элемент %s не найден", data));
        }
        users.put(data.getId(), data);
        return data;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long id) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException(String.format("Элемент с id %s не найден", id));
        }
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        if (!users.containsKey(id)) {
            throw new DataNotFoundException(String.format("Элемент с id %s не найден", id));
        }
        users.remove(id);
    }
}
