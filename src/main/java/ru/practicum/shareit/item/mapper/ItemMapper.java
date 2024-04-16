package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemCreateRequest request);

    Item toItem(ItemUpdateRequest request);

    ItemResponse toResponse(Item item);
}
