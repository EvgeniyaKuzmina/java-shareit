package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {

    private User user = User.builder()
            .name("user")
            .email("user@user.ru")
            .build();
    private Item item = Item.builder().name("Item1").description("Item").available(true).owner(user).build();

    private Comment comment = Comment.builder()
            .text("new comment")
            .item(item)
            .author(user)
            .creat(LocalDateTime.now().minusHours(1))
            .build();
    private Comment comment2 = Comment.builder()
            .text("new comment2")
            .item(item)
            .author(user)
            .creat(LocalDateTime.now())
            .build();

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    // проверка получения списка комментариев по id создателя комментария, отсортированные по дате создания по убыванию
    @Test
    void testFindAllByItemIdOrderByCreatDesc() {
        user = userRepository.save(user);
        item = itemRepository.save(item);
        comment = commentRepository.save(comment);
        comment2 = commentRepository.save(comment2);
        Collection<Comment> comments = commentRepository.findAllByItemIdOrderByCreatDesc(item.getId());

        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(2).contains(comment, comment2);
        assertThat(comments.toArray()[0]).isEqualTo(comment2);
        assertThat(comments.toArray()[1]).isEqualTo(comment);
    }
}