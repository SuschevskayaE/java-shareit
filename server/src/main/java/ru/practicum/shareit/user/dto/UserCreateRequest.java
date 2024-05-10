package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserCreateRequest {

    private final String name;
    private final String email;
}
