package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemUpdateRequest {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
