package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemRequest {

    private final Long id;
    private final String description;
    private final Long requestor;
    private final LocalDateTime created;
}
