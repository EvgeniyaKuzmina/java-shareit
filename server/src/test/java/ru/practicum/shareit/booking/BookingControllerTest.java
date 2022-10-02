package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final User user2 = User.builder().id(2L).name("User2").email("user2@user.com").build();
    private final Item item = Item.builder().id(1L).name("Item1").description("Item").available(true).owner(user).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .itemId(item.getId())
            .start(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0, 0))
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0, 0))
            .status(Status.WAITING)
            .build();
    private final Booking bookingApproved = Booking.builder()
            .id(1L)
            .booker(user2)
            .item(item)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0, 0))
            .status(Status.APPROVED)
            .build();

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mvc;

    // проверка создания бронирования
    @Test
    void testCreateBooking() throws Exception {
        Mockito.when(bookingService.creatNewBooking(any(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.booker.id", is(2L), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItemId()), Long.class));
    }

    // проверка изменения бронирования владельцем вещи
    @Test
    void testProcessingBooking() throws Exception {
        Mockito.when(bookingService.processingBookingRequest(any(), anyLong(), anyBoolean()))
                .thenReturn(bookingApproved);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItemId()), Long.class));
    }


    // проверка получения бронирования по id
    @Test
    void testGetBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .queryParam("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.booker.id", is(2L), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItemId()), Long.class));
    }

    // проверка получения бронирований по id пользователя, который создавал бронирования
    @Test
    void testGetBookingByBookerId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(bookingService.getBookingByBookerId(anyString(), anyLong(), eq(pageable)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .queryParam("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].booker.id", is(2L), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItemId()), Long.class));
    }

    // проверка получения бронирований по id владельца вещей, на которые делали бронирования
    @Test
    void getBookingItemByOwnerId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(bookingService.getBookingItemByOwnerId(anyString(), anyLong(), eq(pageable)))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .queryParam("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].booker.id", is(2L), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItemId()), Long.class));
    }
}