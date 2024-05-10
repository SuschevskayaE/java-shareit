package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.entity.UserEntity;

import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Set<UserEntity> findAllByEmailAndIdNot(String email, Long id);
}
