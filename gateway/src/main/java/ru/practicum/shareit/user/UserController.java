package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * контроллер для работы с пользователями
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    // создание пользователя
    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.warn("gateway: UserController.createUser: Не указан email пользователя");
            throw new ArgumentNotValidException("Не указан email пользователя");
        }

        return userClient.createUser(userDto);
    }

    // обновление пользователя
    @PatchMapping(value = {"/{id}"})
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        return userClient.updateUser(userDto, id);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<Object>  removeUser(@PathVariable Long id) {
        return userClient.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userClient.getUserById(id);
    }

    // получение списка всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }
}

