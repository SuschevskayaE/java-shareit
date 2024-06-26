package ru.practicum.shareit.request.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.controller.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestResponse {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponse> items;
}
