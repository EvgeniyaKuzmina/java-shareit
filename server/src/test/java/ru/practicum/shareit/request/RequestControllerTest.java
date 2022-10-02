package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(1L).requester(user).description("запрос вещи").build();
    private final Item item = Item.builder().id(1L).name("Item1").description("Item").available(true).owner(user).itemRequest(itemRequest).build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("запрос вещи").build();
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestServiceImpl requestService;
    @MockBean
    private ItemServiceImpl itemService;
    @Autowired
    private MockMvc mvc;

    // проверка создания запроса
    @Test
    void testCreateRequest() throws Exception {
        Mockito.when(requestService.createRequest(any(), anyLong()))
                .thenReturn(itemRequest);
        Mockito.when(itemService.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated())))
                .andExpect(jsonPath("$.requesterId", is(itemRequest.getRequester().getId()), Long.class));
    }

    // проверка получения всех запросов пользователя
    @Test
    void testGetAllRequestsByUserId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(requestService.getAllRequestsByUserId(anyLong(), eq(pageable)))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(itemRequest.getCreated())))
                .andExpect(jsonPath("$.[0].requesterId", is(itemRequest.getRequester().getId()), Long.class));
    }


    // проверка получения всех запросов других пользователей, не включая запросы текущего пользователя
    @Test
    void testGetAllRequestsCreatedAnotherUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(requestService.getAllRequestsCreatedAnotherUsers(anyLong(), eq(pageable)))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(itemRequest.getCreated())))
                .andExpect(jsonPath("$.[0].requesterId", is(itemRequest.getRequester().getId()), Long.class));
    }

    // проверка получения запроса по Id
    @Test
    void testGetRequestById() throws Exception {
        Mockito.when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreated())))
                .andExpect(jsonPath("$.requesterId", is(itemRequest.getRequester().getId()), Long.class));
    }
}