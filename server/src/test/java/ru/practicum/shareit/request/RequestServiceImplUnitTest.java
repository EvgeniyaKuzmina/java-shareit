package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

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
class RequestServiceImplUnitTest {

    @Mock
    private final RequestRepository requestRepository;
    @Mock
    private final UserService userService;
    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final User user2 = User.builder().id(2L).name("User2").email("user2@user.com").build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(1L).requester(user).description("запрос вещи").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("запрос вещи").requesterId(user.getId()).build();
    @Mock
    private RequestServiceImpl requestService;

    @BeforeEach
    void beforeEach() {
        requestService = new RequestServiceImpl(requestRepository, userService);
    }

    // тестируем создание нового запроса
    @Test
    void testCreateRequest() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        Mockito.when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(this.itemRequest);

        ItemRequest itemRequest = requestService.createRequest(itemRequestDto, user.getId());

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(itemRequestDto.getRequesterId()));
    }

    // тестируем получение всех запросов пользователя
    @Test
    void testGetAllRequestsByUserId() throws ObjectNotFountException {
        final Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        Mockito.when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequest> itemRequests = requestService.getAllRequestsByUserId(user.getId(), pageable);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests, equalTo(List.of(itemRequest)));
    }

    // тестируем получение всех запросов других пользователей
    @Test
    void testGetAllRequestsCreatedAnotherUsers() throws ObjectNotFountException {
        final Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user2);

        Mockito.when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequest> itemRequests = requestService.getAllRequestsCreatedAnotherUsers(user2.getId(), pageable);

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests, equalTo(List.of(itemRequest)));

    }

    // тестируем получение запроса по id
    @Test
    void testGetRequestByCorrectId() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequest getItemRequest = requestService.getRequestById(1L, user.getId());
        assertThat(getItemRequest.getId(), notNullValue());
        assertThat(getItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(getItemRequest.getRequester(), equalTo(itemRequest.getRequester()));
    }

    // тестируем получение запроса по неверному id
    @Test
    void testGetRequestByIncorrectIdThrowException() throws ObjectNotFountException {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Mockito.when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> requestService.getRequestById(1L, 1L));

        Assertions.assertEquals("Запроса с указанным id " + 1 + " нет", exception.getMessage());
    }
}