package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final Item item = Item.builder().id(1L).name("Item1").description("Item").available(true).owner(user).build();
    private final Comment comment = Comment.builder().text("new comment").author(user).creat(LocalDateTime.now()).item(item).build();
    private final Item updItem = Item.builder().id(1L).name("updItem").description("updItem").available(true).owner(user).build();
    private final ItemDto itemDto = ItemDto.builder().id(1L).name("Item1").description("Item").available(true).ownerId(user.getId()).build();
    private final CommentDto commentDto = CommentDto.builder().text("new comment").creat(LocalDateTime.now()).authorName(user.getName()).build();
    private final Collection<Booking> bookings = new ArrayList<>();

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemServiceImpl itemService;
    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateItem() throws Exception {
        Mockito.when(itemService.createItem(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void testUpdateItem() throws Exception {
        Mockito.when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(updItem);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updItem.getName())))
                .andExpect(jsonPath("$.description", is(updItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updItem.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(updItem.getOwner().getId()), Long.class));
    }

    @Test
    void testRemoveItem() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemById() throws Exception {
        Mockito.when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void testGetAllItemByUserId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(itemService.getAllItemByUserId(anyLong(), eq(pageable)))
                .thenReturn(List.of(item));

        mvc.perform(get("/items?from=1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$.[0].ownerId", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void testSearchItemByNameOrDescription() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(itemService.searchItemByNameOrDescription(anyString(), eq(pageable)))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search?text=iTem&from=1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$.[0].ownerId", is(item.getOwner().getId()), Long.class));
    }

    @Test
    void testAddNewComment() throws Exception {
        Mockito.when(bookingService.getAllBookingByBookerIdSortAsc(anyLong()))
                .thenReturn(bookings);
        Mockito.when(itemService.addNewComment(any(), anyLong(), anyLong(), anyList()))
                .thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }
}