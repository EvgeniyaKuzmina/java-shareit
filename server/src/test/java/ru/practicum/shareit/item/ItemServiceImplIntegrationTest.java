package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private User user;
    private User user2;
    private Item item;

    @BeforeEach
    public void beforeEach() throws ConflictException, ArgumentNotValidException, ObjectNotFountException {
        UserDto userDto = UserDto.builder().name("User1").email("user1@user.com").build();
        UserDto userDto2 = UserDto.builder().name("User2").email("user2@user.com").build();
        ItemDto itemDto = ItemDto.builder().name("название вещи").description("описание")
                .available(true).ownerId(userDto.getId()).build();

        user = userService.createUser(userDto);
        user2 = userService.createUser(userDto2);
        item = itemService.createItem(itemDto, user.getId());
    }

    @AfterEach
    public void afterEach() throws ArgumentNotValidException, ObjectNotFountException, ValidationException {
        itemService.removeItem(item.getId(), user.getId());
        userService.removeUser(user.getId());
        userService.removeUser(user2.getId());
    }


    // проверка сохранения в БД и получения из БД списка вещей
    @Test
    void testGetAllItemByUserId() throws ObjectNotFountException {
        final Pageable pageable = PageRequest.of(0, 10);
        Collection<Item> items = itemService.getAllItemByUserId(user.getId(), pageable);

        assertThat(items.size(), equalTo(1));
        assertThat(items, equalTo(List.of(item)));
    }

    // проверка получения из БД списка вещей по части названия или описания
    @Test
    void testSearchItemByNameOrDescription() {
        final Pageable pageable = PageRequest.of(0, 10);
        Collection<Item> items1 = itemService.searchItemByNameOrDescription("наЗВа", pageable);
        Collection<Item> items2 = itemService.searchItemByNameOrDescription("описаНИЕ", pageable);

        assertThat(items1.size(), equalTo(1));
        assertThat(items1, equalTo(List.of(item)));

        assertThat(items2.size(), equalTo(1));
        assertThat(items2, equalTo(List.of(item)));
    }
}