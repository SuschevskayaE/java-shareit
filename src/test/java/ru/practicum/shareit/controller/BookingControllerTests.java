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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.DataNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    private static final String PATH = "/bookings";
    private static final String PATH_WITH_ID = "/bookings/1";
    private static final String PATH_OWNER = "/bookings/owner";
    private static final long BOOKING_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    private LocalDateTime start;
    private LocalDateTime end;

    @Test
    void create() throws Exception {
        start = LocalDateTime.now().plusMinutes(2);
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(start, end, 1L);

        when(service.create(anyLong(), any())).thenReturn(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(bookingResponse)));
    }

    @Test
    void createValidationException() throws Exception {
        start = LocalDateTime.now().plusMinutes(20);
        end = LocalDateTime.now();
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(start, end, 1L);

        when(service.create(anyLong(), any())).thenThrow(new ValidationException(String.format("Вещь с id %s недоступна для заказа", item.getId())));

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createDataNotFoundException() throws Exception {
        start = LocalDateTime.now().plusMinutes(20);
        end = LocalDateTime.now().plusDays(2);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, user, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(start, end, 1L);

        when(service.create(anyLong(), any())).thenThrow(new DataNotFoundException(String.format("Пользователь c id %s владелец вещи %s", user.getId(), item.getId())));

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createDataNotFoundExceptionHandler() throws Exception {
        start = LocalDateTime.now().plusMinutes(20);
        end = LocalDateTime.now().plusDays(2);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, user, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(start, end, 1L);

        when(service.create(anyLong(), any())).thenThrow(new ValidationException(String.format("Вещь с id %s недоступна для заказа", item.getId())));

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateRequest))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void approved() throws Exception {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);


        when(service.approved(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(bookingResponse)));
    }

    @Test
    void getById() throws Exception {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);


        when(service.get(anyLong(), anyLong())).thenReturn(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(bookingResponse)));
    }

    @Test
    void getUserAll() throws Exception {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);


        when(service.getUserAll(anyLong(), any(), any())).thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(bookingResponse))));
    }

    @Test
    void getUserAllEnumConverter() throws Exception {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);


        when(service.getUserAll(anyLong(), any(), any())).thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FF")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getOwnerAll() throws Exception {
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "Sasha", "Sasha@mail.ru");
        Item item = new Item(3L, "Item", "Item 1", true, null, null, null, null, null);

        BookingResponse bookingResponse = new BookingResponse(BOOKING_ID, start, end, item, user, Status.WAITING);


        when(service.getOwnerAll(anyLong(), any(), any())).thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_OWNER)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(bookingResponse))));
    }
}
