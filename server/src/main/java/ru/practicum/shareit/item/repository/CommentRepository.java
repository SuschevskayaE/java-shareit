package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.CommentEntity;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByItemId(Long itemId);

    List<CommentEntity> findAllByItemIdIn(Set<Long> itemId);


}
