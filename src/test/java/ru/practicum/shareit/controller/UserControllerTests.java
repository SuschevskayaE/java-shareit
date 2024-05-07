package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exeption.DuplicateException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    private static final String PATH = "/users";

    private static final String PATH_WITH_ID = "/users/1";

    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        UserResponse user = new UserResponse(USER_ID, "Sasha", "Sasha@mail.ru");

        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void update() throws Exception {
        UserResponse user = new UserResponse(USER_ID, "Sasha", "Sasha@mail.ru");

        when(userService.update(anyLong(), any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void updateDuplicateException() throws Exception {
        UserResponse user = new UserResponse(USER_ID, "Sasha", "Sasha@mail.ru");

        when(userService.update(anyLong(), any())).thenThrow(new DuplicateException(String.format("Email %s уже существует", user.getEmail())));

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void getById() throws Exception {
        UserResponse user = new UserResponse(USER_ID, "Sasha", "Sasha@mail.ru");

        when(userService.get(anyLong())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void usersAll() throws Exception {
        UserResponse user = new UserResponse(USER_ID, "Sasha", "Sasha@mail.ru");

        when(userService.getAll()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(user))));
    }
}
