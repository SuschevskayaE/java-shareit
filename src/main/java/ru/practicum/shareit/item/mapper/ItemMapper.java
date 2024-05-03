package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item toItem(ItemCreateRequest request);

    Item toItem(ItemUpdateRequest request);

    ItemResponse toResponse(Item item);

    ItemUsersResponse toUsersResponse(Item item);

    ItemUserCommentsResponse toUserCommentsResponse(Item item);
}
