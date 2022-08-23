package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Comment;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment addNewComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Collection<Comment> findAllByItemIdOrderByCreatDesc(Long itemId) {
        return commentRepository.findAllByItemIdOrderByCreatDesc(itemId);
    }

}
