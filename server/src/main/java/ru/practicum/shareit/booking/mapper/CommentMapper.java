package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.user.model.User;


public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .creat(comment.getCreat())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .item(item)
                .text(commentDto.getText())
                .author(user)
                .creat(commentDto.getCreat())
                .build();
    }
}
