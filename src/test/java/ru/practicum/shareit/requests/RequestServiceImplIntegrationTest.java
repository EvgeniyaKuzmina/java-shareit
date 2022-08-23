package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
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
class RequestServiceImplIntegrationTest {

    private final RequestServiceImpl requestService;
    private final UserServiceImpl userService;
    private User user;


    @BeforeEach
    public void beforeEach() throws ConflictException {
        UserDto userDto = UserDto.builder().name("User1").email("user1@user.com").build();
        user = userService.createUser(userDto);
    }

    @AfterEach
    public void afterEach() throws ObjectNotFountException {
        userService.removeUser(user.getId());

    }

    // проверка создания запроса и внесение его в БД
    @Test
    void testCreateRequest() throws ObjectNotFountException {
        final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("запрос вещи").created(LocalDateTime.now()).build();
        ItemRequest getItemRequest = requestService.createRequest(itemRequestDto, user.getId());

        assertThat(getItemRequest.getId(), notNullValue());
        assertThat(getItemRequest.getRequester().getId(), equalTo(user.getId()));
        assertThat(getItemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(getItemRequest.getCreated(), notNullValue());
        requestService.removeRequest(getItemRequest.getId());
    }

    // проверка получения из БД списка всех запросов пользователя
    @Test
    void testGetAllRequestsByUserId() throws ObjectNotFountException {
        final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("запрос вещи").created(LocalDateTime.now()).build();
        ItemRequest getItemRequest = requestService.createRequest(itemRequestDto, user.getId());
        final Pageable pageable = PageRequest.of(0, 10);
        Collection<ItemRequest> itemRequests = requestService.getAllRequestsByUserId(user.getId(), pageable);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests, equalTo(List.of(getItemRequest)));

        requestService.removeRequest(getItemRequest.getId());
    }
}