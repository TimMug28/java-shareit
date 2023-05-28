package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;
    @Column(name = "text", nullable = false, length = 512)
    private String text;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonAlias({"authorName"})
    private User authorName;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
