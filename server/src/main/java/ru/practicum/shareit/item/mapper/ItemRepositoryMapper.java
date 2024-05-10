package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserRepositoryMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRepositoryMapper {

    Item toItem(ItemEntity entity, Long userId);

    ItemEntity toEntity(Item item, Long userId);

    @Mapping(target = "id", ignore = true)
    void updateEntity(Item item, @MappingTarget ItemEntity entity);
}
