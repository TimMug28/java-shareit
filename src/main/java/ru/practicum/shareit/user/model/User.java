package ru.practicum.shareit.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Data
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotBlank
    @Column(name = "user_name")
    private String name;
    @Email
    @NotBlank
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "owner")
    private List<Item> items;
    @OneToMany(mappedBy = "requestor")
    private List<ItemRequest> requests;
    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;
//    @OneToMany(mappedBy = "author")
//    private List<String> comments;
}
