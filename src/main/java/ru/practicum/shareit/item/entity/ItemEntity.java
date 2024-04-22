package ru.practicum.shareit.item.entity;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.entity.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
public class ItemEntity {

    @Id
    @SequenceGenerator(name = "pk_seq_items", sequenceName = "item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq_items")
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    private Long request;
}
