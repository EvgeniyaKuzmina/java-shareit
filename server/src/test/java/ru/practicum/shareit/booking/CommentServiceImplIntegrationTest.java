package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final CommentService commentService;
    private User user;
    private Item item;
    private Item item2;
    private Comment comment;
    private Comment comment2;


    @BeforeEach
    public void beforeEach() throws ConflictException, ArgumentNotValidException, ObjectNotFountException, ValidationException {
        UserDto userDto = UserDto.builder().name("User1").email("user1@user.com").build();
        ItemDto itemDto = ItemDto.builder().name("название вещи").description("описание")
                .available(true).ownerId(userDto.getId()).build();
        ItemDto itemDto2 = ItemDto.builder().name("название вещи2").description("описание")
                .available(true).ownerId(userDto.getId()).build();

        user = userService.createUser(userDto);
        item = itemService.createItem(itemDto, user.getId());
        item2 = itemService.createItem(itemDto2, user.getId());

        comment = Comment.builder()
                .text("new comment")
                .item(item)
                .author(user)
                .creat(LocalDateTime.now().minusHours(1))
                .build();
        comment2 = Comment.builder()
                .text("new comment2")
                .item(item2)
                .author(user)
                .creat(LocalDateTime.now())
                .build();
    }

    @AfterEach
    public void afterEach() throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        itemService.removeItem(item.getId(), user.getId());
        itemService.removeItem(item2.getId(), user.getId());
        userService.removeUser(user.getId());
    }

    @Test
    void addNewComment() {

        Comment getComment = commentService.addNewComment(comment);

        assertThat(getComment.getId(), notNullValue());
        assertThat(getComment.getText(), equalTo(comment.getText()));
        assertThat(getComment.getCreat(), equalTo(comment.getCreat()));
        assertThat(getComment.getAuthor(), equalTo(comment.getAuthor()));
        assertThat(getComment.getItem(), equalTo(comment.getItem()));

        commentService.removeComment(getComment.getId());
    }

    @Test
    void findAllByItemIdOrderByCreatDesc() {
        Comment getComment = commentService.addNewComment(comment); //  создали комментарий к вещи 1
        Comment getComment2 = commentService.addNewComment(comment2); //создали комментарий к вещи 2
        Collection<Comment> comments = commentService.findAllByItemIdOrderByCreatDesc(item.getId());  // получаем комментарии к вещи 1

        assertThat(comments.size(), equalTo(1));
        assertThat(comments, equalTo(List.of(getComment)));

        commentService.removeComment(getComment.getId());
        commentService.removeComment(getComment2.getId());
    }
}