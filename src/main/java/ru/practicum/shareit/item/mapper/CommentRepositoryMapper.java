package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserRepositoryMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentRepositoryMapper {

    Comment toComment(CommentEntity entity);

    CommentEntity toEntity(Comment comment);
}
