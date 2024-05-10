package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(source = "requestId", target = "request.id")
    Item toItem(ItemCreateRequest request);

    @Mapping(source = "requestId", target = "request.id")
    Item toItem(ItemUpdateRequest request);

    @Mapping(source = "request.id", target = "requestId")
    ItemResponse toResponse(Item item);

    ItemUserCommentsResponse toUserCommentsResponse(Item item);
}
