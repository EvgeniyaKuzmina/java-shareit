package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;
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

    @Test
    void testGetAllUsers() {
        Collection<User> users = userService.getAllUsers();
        System.out.println(users);

        assertThat(users.size(), equalTo(1));
        assertThat(users, equalTo(List.of(user)));

    }

    @Test
    void testGetUserByCorrectId() throws ObjectNotFountException {
        User getUser = userService.getUserById(user.getId());

        assertThat(getUser.getId(), equalTo(user.getId()));
        assertThat(getUser.getName(), equalTo(user.getName()));
        assertThat(getUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void testGetUserByIncorrectId() {

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> userService.getUserById(10L));

        Assertions.assertEquals("Пользователя с указанным id " + 10 + " нет", exception.getMessage());
    }
}