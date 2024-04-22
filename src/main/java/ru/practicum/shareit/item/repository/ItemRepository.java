package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findAllByOwnerIdOrderById(Long ownerId);

    Optional<ItemEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("select item from ItemEntity item " +
            "where item.available = true " +
            "and (upper(item.name) like upper(concat('%', :text, '%')) " +
            "or upper(item.description) like upper(concat('%', :text, '%'))) " +
            "and :text != '' ")
    List<ItemEntity> search(@Param("text") String text);
}
