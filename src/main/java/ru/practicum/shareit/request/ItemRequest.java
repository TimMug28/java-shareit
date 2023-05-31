package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "requester_id")
    private Integer requestor;
    @Column(name = "created_date")
    private LocalDateTime created;
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
}

