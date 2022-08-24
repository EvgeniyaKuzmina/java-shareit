package ru.practicum.shareit.user;

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
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplUnitTest {

    private final User user = User.builder().id(1L).name("User1").email("user1@user.com").build();
    private final UserDto userDto = UserDto.builder().id(1L).name("User1").email("user1@user.com").build();
    private final User updUser = User.builder().id(1L).name("UserUpd").email("userUpd@user.com").build();
    private final UserDto updUserDto = UserDto.builder().id(1L).name("UserUpd").email("userUpd@user.com").build();
    @Mock
    private final UserRepository userRepository;
    private UserService userService;


    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    // проверка создания пользователя
    @Test
    void testCreateUser() throws ConflictException {

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User user = userService.createUser(userDto);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    // проверка создания пользователя с неверными данными
    @Test
    void testCreateSameUserWithException() {
        Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Пользователь с такими данными уже существует"));

        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.createUser(userDto));

        Assertions.assertEquals("Пользователь с таким email " + userDto.getEmail() + " уже существует.", exception.getMessage());
    }

    // проверка обновления пользователя с неверными данными
    @Test
    void testUpdateUserWithException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(updUser));
        Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Пользователь с такими данными уже существует"));

        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.updateUser(userDto, userDto.getId()));

        Assertions.assertEquals("Пользователь с таким email " + userDto.getEmail() + " уже существует.", exception.getMessage());
    }

    // проверка обновления пользователя
    @Test
    void testUpdateUser() throws ConflictException, ObjectNotFountException {

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(updUser);

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(updUser));

        User user = userService.updateUser(updUserDto, updUserDto.getId());

        assertThat(user.getId(), equalTo(updUserDto.getId()));
        assertThat(user.getName(), equalTo(updUserDto.getName()));
        assertThat(user.getEmail(), equalTo(updUserDto.getEmail()));
    }

    @Test
    void testRemoveUser() throws ObjectNotFountException {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.removeUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void testGetAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        Collection<User> users = userService.getAllUsers();

        assertThat(users.size(), equalTo(1));
        assertThat(users, equalTo(List.of(user)));
    }

    @Test
    void testGetUserByCorrectId() throws ObjectNotFountException {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User getUser = userService.getUserById(1L);

        assertThat(getUser.getId(), equalTo(user.getId()));
        assertThat(getUser.getName(), equalTo(user.getName()));
        assertThat(getUser.getEmail(), equalTo(user.getEmail()));

    }

    @Test
    void testGetUserByIncorrectIdThrowException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFountException exception = Assertions.assertThrows(
                ObjectNotFountException.class,
                () -> userService.getUserById(10L));

        Assertions.assertEquals("Пользователя с указанным id " + 10 + " нет", exception.getMessage());
    }
}