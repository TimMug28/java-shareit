package ru.practicum.shareit.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
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
    @JsonIgnore
    private List<Item> items;
    @OneToMany(mappedBy = "booker")
    @JsonIgnoreProperties("booker")
    private List<Booking> bookings;
    @JsonIgnore
    @OneToMany(mappedBy = "authorName")
    private List<Comment> comments;

    public User() {
        this.items = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }
}
