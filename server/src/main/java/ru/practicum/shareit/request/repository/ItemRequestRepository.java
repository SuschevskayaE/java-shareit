package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.entity.ItemRequestEntity;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Long> {

    List<ItemRequestEntity> findAllByRequestorIdOrderByCreated(Long requestorId);

    @Query("select item_requests from ItemRequestEntity item_requests " +
            "where item_requests.requestor.id != :user_id " +
            "order by item_requests.created desc ")
    List<ItemRequestEntity> getAll(@Param("user_id") Long userId, Pageable pageable);
}
