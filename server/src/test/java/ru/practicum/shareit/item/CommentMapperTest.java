package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapperTest {

    @Test
    public void testToDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("комментарий");
        User author = new User();
        author.setName("user");
        comment.setAuthorName(author);
        comment.setCreatedDate(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toDTO(comment);

        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getAuthorName().getName(), commentDto.getAuthorName());
        Assertions.assertEquals(comment.getCreatedDate(), commentDto.getCreatedDate());
    }

    @Test
    public void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("комментарий");
        commentDto.setAuthorName("user");
        commentDto.setCreatedDate(LocalDateTime.now());

        User author = new User();
        author.setName(commentDto.getAuthorName());

        Comment comment = CommentMapper.toComment(commentDto, author);

        Assertions.assertEquals(commentDto.getId(), comment.getId());
        Assertions.assertEquals(commentDto.getText(), comment.getText());
        Assertions.assertEquals(commentDto.getCreatedDate(), comment.getCreatedDate());
        Assertions.assertEquals(author, comment.getAuthorName());
    }
}
