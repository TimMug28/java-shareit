package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toDTO(Comment comment) {
        CommentDto commentDTO = new CommentDto();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setItem(comment.getItem());
        commentDTO.setAuthorName(comment.getAuthorName().getName());
        commentDTO.setCreatedDate(comment.getCreatedDate());
        return commentDTO;
    }

    public static Comment toComment(CommentDto commentDTO, Item item, User author) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setText(commentDTO.getText());
        comment.setItem(item);
        comment.setAuthorName(author);
        comment.setCreatedDate(commentDTO.getCreatedDate());
        return comment;
    }
}
