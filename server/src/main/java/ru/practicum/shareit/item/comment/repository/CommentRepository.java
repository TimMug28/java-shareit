package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
