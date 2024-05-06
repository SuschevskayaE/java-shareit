package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestCreateRequest request);

    ItemRequestResponse toResponse(ItemRequest itemRequest);
}
