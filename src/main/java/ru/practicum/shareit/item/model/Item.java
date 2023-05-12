package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "item_name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne(mappedBy = "item")
    private ItemRequest request;
    @OneToMany(mappedBy = "item")
    private List<Booking> bookings;
}
