package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
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
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final CommentService commentService;
    private Booking booking;
    private User user;
    private User user2;
    private Item item;


    @BeforeEach
    public void beforeEach() throws ConflictException, ArgumentNotValidException, ObjectNotFountException, ValidationException {
        UserDto userDto = UserDto.builder().name("User1").email("user1@user.com").build();
        UserDto userDto2 = UserDto.builder().name("User2").email("user2@user.com").build();
        ItemDto itemDto = ItemDto.builder().name("название вещи").description("описание")
                .available(true).ownerId(userDto.getId()).build();

        user = userService.createUser(userDto);
        user2 = userService.createUser(userDto2);
        item = itemService.createItem(itemDto, user.getId());

        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 8, 19, 15, 0))
                .end(LocalDateTime.of(2023, 9, 19, 15, 0))
                .build();

        booking = bookingService.creatNewBooking(bookingDto, user2.getId());

    }

    @AfterEach
    public void afterEach() throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        bookingService.removeBooking(booking.getId());
        itemService.removeItem(item.getId(), user.getId());
        userService.removeUser(user.getId());
        userService.removeUser(user2.getId());
    }

    // создание бронирования, сохранение в БД и получение данных из БД
    @Test
    void testCreatNewBooking() throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2024, 8, 19, 15, 0))
                .end(LocalDateTime.of(2024, 9, 19, 15, 0))
                .build();
        Booking booking = bookingService.creatNewBooking(bookingDto, user2.getId());

        assertThat(booking.getId(), notNullValue());
        MatcherAssert.assertThat(booking.getBooker().getId(), equalTo(user2.getId()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getItem(), equalTo(item));

        bookingService.removeBooking(booking.getId());
    }

    // проверяем получение списка бронирований всех вещей пользователя
    @Test
    void testGetBookingItemByOwnerId() throws ObjectNotFountException, ValidationException {
        Pageable pageable = PageRequest.of(0, 10);

        Collection<Booking> bookings = bookingService.getBookingItemByOwnerId("ALL", user.getId(), pageable);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings, equalTo(List.of(booking)));

    }

    // получение списка комментариев по id вещи
    @Test
    void testGetAllCommentsByItemIdOrderByCreatDesc() {
        Comment comment = Comment.builder().text("comment")
                .creat(LocalDateTime.of(2022, 8, 19, 15, 0))
                .author(user)
                .item(item)
                .build();
        Comment getComment = commentService.addNewComment(comment);

        Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(item.getId());

        assertThat(comments.size(), equalTo(1));
        assertThat(comments, equalTo(List.of(getComment)));
    }
}