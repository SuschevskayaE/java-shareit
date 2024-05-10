package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    Comment toComment(CommentCreateRequest request);

    @Mapping(source = "author.name", target = "authorName")
    CommentResponse toResponse(Comment comment);
}
