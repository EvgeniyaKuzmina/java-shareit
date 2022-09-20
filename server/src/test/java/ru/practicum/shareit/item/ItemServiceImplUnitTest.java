package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.CommentServiceImpl;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplUnitTest {

    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserServiceImpl userService;
    @Mock
    private final CommentServiceImpl commentService;
    @Mock
    private final RequestServiceImpl requestService;
    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final Item item = Item.builder().id(1L).name("Item1").description("Item").available(true).owner(user).build();
    private final Booking booking = Booking.builder().id(1L).booker(user).item(item)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0))
            .build();
    private final Booking pastBooking = Booking.builder().id(1L).booker(user).item(item)
            .start(LocalDateTime.of(2020, 8, 19, 15, 0))
            .end(LocalDateTime.of(2020, 9, 19, 15, 0))
            .build();
    private final Item updItem = Item.builder().id(1L).name("updItem").description("updItem").available(true).owner(user).build();
    private final ItemDto itemDto = ItemDto.builder().id(1L).name("Item1").description("Item").available(true).ownerId(user.getId()).build();
    private final ItemDto updItemDto = ItemDto.builder().id(1L).name("updItem").description("updItem").available(true).ownerId(user.getId()).build();
    private final Comment comment = Comment.builder().id(1L).text("comment")
            .creat(LocalDateTime.of(2022, 8, 19, 15, 0))
            .author(user).build();
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("comment")
            .creat(LocalDateTime.of(2022, 8, 19, 15, 0))
            .authorName(user.getName()).build();
    @Mock
    private ItemService itemService;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(userService, itemRepository, commentService, requestService);

    }

    // проверка добавления вещи
    @Test
    void testCreateItem() throws ArgumentNotValidException, ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Item item = itemService.createItem(itemDto, 1L);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }


    // проверка обновления вещи
    @Test
    void testUpdateItem() throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(updItem);

        Item item = itemService.updateItem(updItemDto, updItemDto.getId(), user.getId());

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(updItemDto.getName()));
        assertThat(item.getDescription(), equalTo(updItemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwnerId()));
    }

    // проверка удаления вещи с корректным id
    @Test
    void testRemoveItem() throws ArgumentNotValidException, ValidationException, ObjectNotFountException {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        itemService.removeItem(1L, user.getId());

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(1L);
    }

    // проверяем что нельзя удалить вещи, если указан неверный id владельца
    @Test
    void testRemoveItemWithArgumentNotValidException() throws ObjectNotFountException {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        final ArgumentNotValidException exception = Assertions.assertThrows(
                ArgumentNotValidException.class,
                () -> itemService.removeItem(1L, 2L));

        Assertions.assertEquals("Указан неверный id  владельца вещи", exception.getMessage());

    }


    // проверяем получение вещи с корректным id
    @Test
    void getItemByCorrectId() throws ObjectNotFountException {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item getItem = itemService.getItemById(1L);

        assertThat(getItem.getId(), notNullValue());
        assertThat(getItem.getName(), equalTo(item.getName()));
        assertThat(getItem.getDescription(), equalTo(item.getDescription()));
        assertThat(getItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(getItem.getOwner().getId(), equalTo(item.getId()));
    }

    // проверяем получение вещи с неверным id
    @Test
    void getItemByIncorrectId() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> itemService.getItemById(10L));

        Assertions.assertEquals("Вещи с таким id нет", exception.getMessage());
    }


    // проверяем получение списка вещей по id владельца
    @Test
    void testGetAllItemByUserId() throws ObjectNotFountException {
        final Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), eq(pageable)))
                .thenReturn(List.of());

        Collection<Item> pageItems = itemService.getAllItemByUserId(user.getId(), pageable);
        assertThat(pageItems.size(), equalTo(0));
        assertThat(pageItems, equalTo(List.of()));
    }


    // проверка, поиск вещи по части названия или описания
    @Test
    void testSearchItemByNameOrDescription() {
        final Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), eq(pageable)))
                .thenReturn(List.of(item));

        Collection<Item> pageItems = itemService.searchItemByNameOrDescription("Item1", pageable);
        assertThat(pageItems.size(), equalTo(1));
        assertThat(pageItems, equalTo(List.of(item)));
    }

    // проверка на добавление комментария
    @Test
    void testAddNewComment() throws ObjectNotFountException, ArgumentNotValidException, ValidationException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(commentService.addNewComment(any()))
                .thenReturn(comment);

        Comment getComment = itemService.addNewComment(commentDto, user.getId(), item.getId(), List.of(pastBooking));
        assertThat(getComment.getId(), notNullValue());
        assertThat(getComment.getText(), equalTo(comment.getText()));
        assertThat(getComment.getAuthor(), equalTo(comment.getAuthor()));
        assertThat(getComment.getCreat(), equalTo(comment.getCreat()));

    }


    // проверка, что нельзя оставить комментарий если указанный пользователь не брал вещь в аренду
    @Test
    void testAddNewCommentWithArgumentNotValidExceptionWrongUserId() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final ArgumentNotValidException exception = Assertions.assertThrows(
                ArgumentNotValidException.class,
                () -> itemService.addNewComment(commentDto, 2L, 2L, List.of(booking)));

        Assertions.assertEquals("Пользователь 2 не делал бронирование указанной вещи", exception.getMessage());
    }

    // проверка, что нельзя оставить комментарий ранее даты аренды
    @Test
    void testAddNewCommentWithArgumentNotValidExceptionWrongBookingDate() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final ArgumentNotValidException exception = Assertions.assertThrows(
                ArgumentNotValidException.class,
                () -> itemService.addNewComment(commentDto, 1L, 1L, List.of(booking)));

        Assertions.assertEquals("Нельзя оставить комментарий ранее даты бронирования", exception.getMessage());
    }

    // получение списка вещей по id запроса
    @Test
    void testFindAllByRequestId() {
        Mockito.when(itemRepository.findAllByItemRequestId(anyLong()))
                .thenReturn(List.of(item));

        Collection<Item> items = itemService.findAllByRequestId(1L);
        assertThat(items.size(), equalTo(1));
        assertThat(items, equalTo(List.of(item)));
    }
}