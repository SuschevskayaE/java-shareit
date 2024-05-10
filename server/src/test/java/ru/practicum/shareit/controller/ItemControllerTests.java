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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUserCommentsResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    private static final String PATH = "/items";
    private static final String PATH_WITH_ID = "/items/1";
    private static final String PATH_WITH_WRONG_ID = "/items/100";
    private static final String PATH_SEARCH = "/items/search";

    private static final String PATH_COMMENT = "/items/1/comment";
    private static final long ITEM_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @Test
    void create() throws Exception {
        ItemResponse itemResponse = new ItemResponse(ITEM_ID, "Item", "Item 1", true, 1L);

        when(service.create(anyLong(), any())).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemResponse))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemResponse)));
    }

    @Test
    void update() throws Exception {
        ItemResponse itemResponse = new ItemResponse(ITEM_ID, "Item", "Item 1", true, 1L);

        when(service.update(anyLong(), anyLong(), any())).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemResponse))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemResponse)));
    }

    @Test
    void getById() throws Exception {
        Item item = new Item(ITEM_ID, "Item", "Item 1", true, null, null, null, null, null);
        CommentResponse commentResponse = new CommentResponse(2L, "comment", item, "Name", LocalDateTime.now());

        ItemUserCommentsResponse itemResponse = new ItemUserCommentsResponse(ITEM_ID, "Item", "Item 1", true, null, null, Collections.singletonList(commentResponse));

        when(service.get(anyLong(), anyLong())).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemResponse)));
    }

    @Test
    void getUsersAll() throws Exception {
        CommentResponse commentResponse = new CommentResponse(2L, "comment", null, "Name", LocalDateTime.now());

        ItemUserCommentsResponse itemResponse = new ItemUserCommentsResponse(ITEM_ID, "Item", "Item 1", true, null, null, Collections.singletonList(commentResponse));

        when(service.getUsersAll(anyLong(), any())).thenReturn(Collections.singletonList(itemResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(itemResponse))));
    }

    @Test
    void getAll() throws Exception {
        ItemResponse itemResponse = new ItemResponse(ITEM_ID, "Item", "Item 1", true, 2L);

        when(service.getAll(anyString(), any())).thenReturn(Collections.singletonList(itemResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_SEARCH)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Text")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(itemResponse))));
    }

    @Test
    void createComment() throws Exception {
        Item item = new Item(ITEM_ID, "Item", "Item 1", true, null, null, null, null, null);

        CommentResponse commentResponse = new CommentResponse(3L, "Text", item, "Name", LocalDateTime.now());


        when(service.createComment(anyLong(), anyLong(), any())).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH_COMMENT)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentResponse))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(commentResponse)));
    }
}
