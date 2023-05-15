package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface CommentRepository extends JpaRepository<Item, Long> {
    }

