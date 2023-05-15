package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toDTO(Comment comment) {
        CommentDto commentDTO = new CommentDto();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setItem(comment.getItem());
        commentDTO.setAuthor(comment.getAuthor());
        commentDTO.setCreatedDate(comment.getCreatedDate());
        return commentDTO;
    }

    public static Comment toComment(CommentDto commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setText(commentDTO.getText());
        Item item = new Item();
        item.setId(commentDTO.getItem().getId());
        comment.setItem(item);
        User author = new User();
        author.setId(commentDTO.getAuthor().getId());
        comment.setAuthor(author);
        comment.setCreatedDate(commentDTO.getCreatedDate());
        return comment;
    }
}
