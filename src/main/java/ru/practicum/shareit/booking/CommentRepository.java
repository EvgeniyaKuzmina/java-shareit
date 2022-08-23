package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // получение списка комментариев по id создателя комментария, отсортированные по дате создания по убыванию
    Collection<Comment> findAllByItemIdOrderByCreatDesc(Long itemId);
}
