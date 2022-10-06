package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Comment;

import java.util.Collection;

public interface CommentService {

    // добавление нового комментария
    Comment addNewComment(Comment comment);

    // получение списка комментариев по id вещи отсортированные по дате создания от более раннего к более позднему
    Collection<Comment> findAllByItemIdOrderByCreatDesc(Long itemId);

    // удаление комментария
    void removeComment(Long commentId);
}
