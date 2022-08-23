package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private final UserDto userDto = UserDto.builder()
            .name("user")
            .email("user@user.ru")
            .build();
    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.ru")
            .build();

    private final User updUser = User.builder()
            .id(1L)
            .name("UserUpd")
            .email("userUpd@user.com")
            .build();

    private final UserDto updUserDto = UserDto.builder()
            .name("UserUpd")
            .email("userUpd@user.com")
            .build();

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mvc;


    // проверяем корректное создание пользователя
    @Test
    void testSaveNewUser() throws Exception {
        Mockito.when(userService.createUser(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    // проверяем что выбрасывается исключение
    @Test
    void testSaveNewUserWithException() throws Exception {
        Mockito.when(userService.createUser(any()))
                .thenThrow(ConflictException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    // обновление данных пользователя
    @Test
    void testUpdateUser() throws Exception {
        Mockito.when(userService.updateUser(any(), anyLong()))
                .thenReturn(updUser);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updUser.getName())))
                .andExpect(jsonPath("$.email", is(updUser.getEmail())));
    }

    // проверяем что выбрасывается исключение при обновлении с неверным Id
    @Test
    void testUpdateUserWithObjectNotFountException() throws Exception {
        Mockito.when(userService.updateUser(any(), anyLong()))
                .thenThrow(ObjectNotFountException.class);

        mvc.perform(patch("/users/2")
                        .content(mapper.writeValueAsString(updUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    // проверяем что выбрасывается исключение при обновлении с уже существующими данными
    @Test
    void testUpdateUserWithConflictException() throws Exception {
        Mockito.when(userService.updateUser(any(), anyLong()))
                .thenThrow(ConflictException.class);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    @Test
    void testRemoveUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void testGetAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(user, updUser));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(user.getName())))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(updUser.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(updUser.getName())))
                .andExpect(jsonPath("$.[1].email", is(updUser.getEmail())));
    }
}