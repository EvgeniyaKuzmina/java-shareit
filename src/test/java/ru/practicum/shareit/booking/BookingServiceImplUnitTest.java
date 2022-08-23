package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplUnitTest {

    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final UserService userService;
    @Mock
    private final CommentService commentService;
    @Mock
    private final ItemService itemService;

    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final User user2 = User.builder().id(2L).name("User2").email("user2@user.com").build();
    private final Item item = Item.builder().id(1L).name("Item1").description("Item").available(true)
            .owner(user).build();
    private final Booking booking = Booking.builder().id(1L)
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0))
            .status(Status.WAITING)
            .build();
    private final Booking pastBooking = Booking.builder().id(2L)
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2020, 8, 19, 15, 0))
            .end(LocalDateTime.of(2020, 9, 19, 15, 0))
            .status(Status.APPROVED)
            .build();
    private final BookingDto bookingDto = BookingDto.builder().itemId(item.getId())
            .start(LocalDateTime.of(2023, 8, 19, 15, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0))
            .build();
    private final Comment comment = Comment.builder().id(1L).text("comment")
            .creat(LocalDateTime.of(2022, 8, 19, 15, 0))
            .author(user).build();

    private BookingService bookingService;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, commentService, userService, itemService);
    }

    //проверка создания нового бронирования
    @Test
    void testCreatNewBooking() throws ObjectNotFountException, ArgumentNotValidException, ValidationException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user2);
        Mockito.when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Booking booking = bookingService.creatNewBooking(bookingDto, 2L);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getBooker(), equalTo(user2));
        assertThat(booking.getStatus(), notNullValue());
    }

    // проверка обработки владельцем вещи бронирования по его вещи, статус бронирования должен быть APPROVED
    @Test
    void testProcessingBookingRequestStatusShouldBeApproved() throws ObjectNotFountException, ValidationException, ArgumentNotValidException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Booking booking = bookingService.processingBookingRequest(1L, 1L, true);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getBooker(), equalTo(user2));
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    // Проверка обработки владельцем вещи бронирования по его вещи.
    // Если передан id не владельца вещи, выбрасывается исключение
    @Test
    void testProcessingBookingRequestWithObjectNotFountException() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> bookingService.processingBookingRequest(1L, 2L, true));

        Assertions.assertEquals("Указан неверный id владельца вещи", exception.getMessage());
    }

    // Проверка обработки владельцем вещи бронирования по его вещи.
    // Если бронирование уже подтверждено, подтвердить его второй раз не получится
    @Test
    void testProcessingBookingRequestWithArgumentNotValidException() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(pastBooking));
        final ArgumentNotValidException exception = Assertions.assertThrows(
                ArgumentNotValidException.class,
                () -> bookingService.processingBookingRequest(1L, 1L, true));

        Assertions.assertEquals("Бронирование уже подтверждено. Нельзя подтвердить бронь дважды", exception.getMessage());
    }

    // проверка обработки владельцем вещи бронирования по его вещи, статус бронирования должен быть REJECTED
    @Test
    void testProcessingBookingRequestStatusShouldBeRejected() throws ObjectNotFountException, ValidationException, ArgumentNotValidException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Booking booking = bookingService.processingBookingRequest(1L, 1L, false);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getBooker(), equalTo(user2));
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    // проверка получения бронирования по корректному id
    @Test
    void testGetBookingById() throws ObjectNotFountException, ValidationException {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking getBooking = bookingService.getBookingById(1L, user2.getId());
        assertThat(getBooking.getId(), notNullValue());
        assertThat(getBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(getBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(getBooking.getStart(), equalTo(booking.getStart()));
        assertThat(getBooking.getItem(), equalTo(booking.getItem()));
    }

    // проверка получения бронирования, если передан неверный id создателя бронирования
    @Test
    void testGetBookingByIncorrectId() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> bookingService.getBookingById(1L, 3L));

        Assertions.assertEquals("Указан неверный id  " + 3 + " создателя бронирования или владельца вещи", exception.getMessage());

    }

    // проверка получения списка бронирований по id пользователя, который делал бронирования
    @Test
    void testGetBookingByBookerId() throws ObjectNotFountException, ValidationException {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(booking));
        Collection<Booking> bookings = bookingService.getBookingByBookerId("ALL", user2.getId(), pageable);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings, equalTo(List.of(booking)));
    }

    // проверка получения списка бронирований конкретной вещи по id владельца вещи
    @Test
    void testGetBookingItemByOwnerId() throws ObjectNotFountException, ValidationException {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemService.getAllItemByUserIdWithoutPagination(anyLong()))
                .thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemIdOrderByStartDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(booking));
        Collection<Booking> bookings = bookingService.getBookingItemByOwnerId("ALL", user.getId(), pageable);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings, equalTo(List.of(booking)));
    }

    // проверка и получение бронирования по корректному Id
    @Test
    void testCheckAndGetBookingByCorrectId() throws ValidationException, ObjectNotFountException {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking getBooking = bookingService.checkAndGetBookingById(1L);
        assertThat(getBooking.getId(), notNullValue());
        assertThat(getBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(getBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(getBooking.getStart(), equalTo(booking.getStart()));
        assertThat(getBooking.getItem(), equalTo(booking.getItem()));
    }

    // проверка и получение бронирования по неверному Id
    @Test
    void testCheckAndGetBookingByIncorrectId() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> bookingService.checkAndGetBookingById(1L));

        Assertions.assertEquals("Бронирования с таким id " + 1L + " нет", exception.getMessage());
    }

    //проверка получения списка всех бронирований по id
    @Test
    void testGetAllBookingByBookerIdSortDesc() throws ValidationException, ObjectNotFountException {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(pastBooking, booking));
        Collection<Booking> bookings = bookingService.getAllBookingByBookerIdSortDesc(user2.getId(), pageable);

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.toArray()[0], equalTo(pastBooking));
        assertThat(bookings.toArray()[1], equalTo(booking));
    }

    // получение списка всех комментариев по id вещи
    @Test
    void testGetAllCommentsByItemIdOrderByCreatDesc() {
        Mockito.when(commentService.findAllByItemIdOrderByCreatDesc(anyLong()))
                .thenReturn(List.of(comment));
        Collection<Comment> comments = bookingService.getAllCommentsByItemIdOrderByCreatDesc(item.getId());

        assertThat(comments.size(), equalTo(1));
        assertThat(comments, equalTo(List.of(comment)));
    }

    //проверяем что из списка бронирований мы получаем последнее ближайшее бронирование от текущей даты и ближайшее следующее бронирование в будущее
    @Test
    void getLastOrNextBookingForItem() {
        Mockito.when(bookingRepository.findAllByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking, pastBooking));
        ItemDto.LastOrNextBooking lastBookingForItem = bookingService.getLastOrNextBookingForItem(item, user.getId(), "LAST");

        assertThat(lastBookingForItem.getId(), equalTo(pastBooking.getId()));
        assertThat(lastBookingForItem.getEnd(), equalTo(pastBooking.getEnd()));
        assertThat(lastBookingForItem.getStart(), equalTo(pastBooking.getStart()));
        assertThat(lastBookingForItem.getBookerId(), equalTo(pastBooking.getBooker().getId()));

        ItemDto.LastOrNextBooking nextBookingForItem = bookingService.getLastOrNextBookingForItem(item, user.getId(), "NEXT");

        assertThat(nextBookingForItem.getId(), equalTo(booking.getId()));
        assertThat(nextBookingForItem.getEnd(), equalTo(booking.getEnd()));
        assertThat(nextBookingForItem.getStart(), equalTo(booking.getStart()));
        assertThat(nextBookingForItem.getBookerId(), equalTo(booking.getBooker().getId()));

    }
}