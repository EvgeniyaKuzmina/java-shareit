package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Comment;

import java.util.Collection;

public interface CommentService {

    Comment saveComment(Comment comment);

    Collection<Comment> findAllByItemIdOrderByCreatDesc(Long itemId);
}
