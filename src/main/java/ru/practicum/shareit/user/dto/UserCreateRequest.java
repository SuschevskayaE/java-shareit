package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class UserCreateRequest {

    @NotNull
    private final String name;

    @Email
    @NotNull
    private final String email;
}
