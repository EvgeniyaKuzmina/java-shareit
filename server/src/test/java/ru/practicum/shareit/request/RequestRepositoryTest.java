package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestRepositoryTest {

    private User user = User.builder()
            .name("user")
            .email("user@user.ru")
            .build();

    private User user2 = User.builder()
            .name("User2")
            .email("user2@user.com")
            .build();

    private ItemRequest itemRequest = ItemRequest.builder()
            .requester(user2)
            .description("запрос вещи")
            .created(LocalDateTime.now())
            .build();

    private ItemRequest itemRequest2 = ItemRequest.builder()
            .requester(user2)
            .description("запрос вещи")
            .created(LocalDateTime.now().minusDays(2))
            .build();

    private ItemRequest itemRequest3 = ItemRequest.builder()
            .requester(user)
            .description("запрос вещи")
            .created(LocalDateTime.now().minusDays(3))
            .build();

    private ItemRequest itemRequest4 = ItemRequest.builder()
            .requester(user)
            .description("запрос вещи")
            .created(LocalDateTime.now().minusDays(1))
            .build();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    public void beforeAll() {
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        itemRequest = requestRepository.save(itemRequest);
        itemRequest2 = requestRepository.save(itemRequest2);
        itemRequest3 = requestRepository.save(itemRequest3);
        itemRequest4 = requestRepository.save(itemRequest4);
    }

    // проверка получения списка всех запросов вещей по id создателя запроса
    @Test
    void testFindAllByRequesterIdOrderByCreatedDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(user2.getId(), pageable);

        Assertions.assertThat(requests).isNotEmpty();
        Assertions.assertThat(requests).hasSize(2).contains(itemRequest, itemRequest2);
        assertThat(requests.get(0)).isEqualTo(itemRequest);
        assertThat(requests.get(1)).isEqualTo(itemRequest2);


    }

    // проверка получения списка всех запросов вещей сделанных другими пользователями, не включая запросы текущего пользователя
    @Test
    void testFindAllByRequesterIdNotOrderByCreatedDesc() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(user2.getId(), pageable);

        Assertions.assertThat(requests).isNotEmpty();
        Assertions.assertThat(requests).hasSize(2).contains(itemRequest3, itemRequest4);
        assertThat(requests.get(0)).isEqualTo(itemRequest4);
        assertThat(requests.get(1)).isEqualTo(itemRequest3);
    }
}