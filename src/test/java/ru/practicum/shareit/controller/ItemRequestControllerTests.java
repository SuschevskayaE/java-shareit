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
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    private static final String PATH = "/requests";
    private static final String PATH_WITH_ID = "/requests/1";
    private static final String PATH_ALL = "/requests/all";
    private static final long REQUEST_ID = 1L;

    private LocalDateTime created;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Test
    void create() throws Exception {
        created = LocalDateTime.now();
        ItemResponse itemResponse = new ItemResponse(1L, "Item", "Item 1", true, null);

        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(REQUEST_ID, "Description", created, Collections.singletonList(itemResponse));

        when(service.create(anyLong(), any())).thenReturn(itemRequestResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemRequestResponse)));
    }

    @Test
    void getById() throws Exception {
        created = LocalDateTime.now();
        ItemResponse itemResponse = new ItemResponse(1L, "Item", "Item 1", true, null);

        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(REQUEST_ID, "Description", created, Collections.singletonList(itemResponse));

        when(service.get(anyLong(), anyLong())).thenReturn(itemRequestResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemRequestResponse)));
    }

    @Test
    void getUsersAll() throws Exception {
        created = LocalDateTime.now();
        ItemResponse itemResponse = new ItemResponse(1L, "Item", "Item 1", true, null);

        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(REQUEST_ID, "Description", created, Collections.singletonList(itemResponse));

        when(service.getUsersAll(anyLong())).thenReturn(Collections.singletonList(itemRequestResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(itemRequestResponse))));
    }

    @Test
    void getAll() throws Exception {
        created = LocalDateTime.now();

        ItemResponse itemResponse = new ItemResponse(1L, "Item", "Item 1", true, null);

        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(REQUEST_ID, "Description", created, Collections.singletonList(itemResponse));

        when(service.getAll(anyLong(), any())).thenReturn(Collections.singletonList(itemRequestResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_ALL)
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(itemRequestResponse))));
    }
}
