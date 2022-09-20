package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

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
    private Item item = Item.builder().name("Item1").description("Item").available(true).owner(user).itemRequest(itemRequest).build();
    private Item item2 = Item.builder().name("Item2").description("Item2").available(false).owner(user).build();
    private Item item3 = Item.builder().name("вещь3").description("описание вещи3").available(true).owner(user2).build();
    private Item item4 = Item.builder().name("вещь4").description("описание").available(true).owner(user2).build();


    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    public void beforeAll() {
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        itemRequest = requestRepository.save(itemRequest);
        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
        item4 = itemRepository.save(item4);
    }

    // проверка получения списка вещей про id пользователя отсортированные в порядке возрастания id
    @Test
    void testFindAllByOwnerIdOrderByIdAscPageFrom0To10() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), pageable);

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(2).contains(item, item2);
        assertThat(items.get(0)).isEqualTo(item);
        assertThat(items.get(1)).isEqualTo(item2);

    }

    // проверка получения списка вещей про id пользователя отсортированные в порядке возрастания id, с ограничением постраничного просмотра
    @Test
    void testFindAllByOwnerIdOrderByIdAscPageFrom0To1() {
        Pageable pageable = PageRequest.of(0, 1);

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), pageable);

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(1).contains(item);
        assertThat(items.get(0)).isEqualTo(item);
    }

    // проверка получения списка вещей про id пользователя отсортированные в порядке возрастания id, с ограничением постраничного просмотра
    @Test
    void testFindAllByOwnerIdOrderByIdAscPageFrom1To1() {
        Pageable pageable = PageRequest.of(1, 1);

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), pageable);

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(1).contains(item2);
        assertThat(items.get(0)).isEqualTo(item2);
    }


    // проверка поиск вещи по части строки в названии или в описании
    @Test
    void testFindByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue("вещь", "вещь", pageable);

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(2).contains(item3, item4);
    }

    // проверка поиск вещи по части строки в названии или в описании, игнорируя регистр
    @Test
    void testFindByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrueWithDifferentCase() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue("опИСАНие", "опИСАНие", pageable);

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(2).contains(item3, item4);
    }

    // проверка поиска всех вещей по id запроса на вещь
    @Test
    void testFindAllByItemRequestId() {
        Collection<Item> items = itemRepository.findAllByItemRequestId(itemRequest.getId());

        Assertions.assertThat(items).isNotEmpty();
        Assertions.assertThat(items).hasSize(1).contains(item);

    }
}