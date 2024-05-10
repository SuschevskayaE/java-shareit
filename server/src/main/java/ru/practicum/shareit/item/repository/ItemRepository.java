package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    Optional<ItemEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("select item from ItemEntity item " +
            "where item.available = true " +
            "and (upper(item.name) like upper(concat('%', :text, '%')) " +
            "or upper(item.description) like upper(concat('%', :text, '%')) " +
            "or :text is null ) " +
            "and :text <> '' ")
    List<ItemEntity> search(@Param("text") String text, Pageable pageable);

    List<ItemEntity> findAllByRequestIdIn(Set<Long> requestId);
}
