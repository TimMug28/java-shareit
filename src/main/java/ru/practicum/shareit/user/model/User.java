package ru.practicum.shareit.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "user_name")
    private String name;
    @Email
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "owner")
    private List<Item> items;
    @OneToMany(mappedBy = "requestor")
    private List<ItemRequest> requests;
    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;
}
