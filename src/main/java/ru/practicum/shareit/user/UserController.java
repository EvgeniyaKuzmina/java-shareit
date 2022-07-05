package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * контроллер для работы с пользователями
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // создание пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) throws ValidationException, ArgumentNotValidException {
        if (userDto.getEmail() == null) {
            throw new ArgumentNotValidException("Не указан email пользователя");
        }
        return userService.createUser(userDto);
    }

    // обновление пользователя
    @PatchMapping(value = {"/{id}"})
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) throws ConflictException, ObjectNotFountException {
        return userService.updateUser(userDto, id);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable Long id) throws ObjectNotFountException {
        userService.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    public UserDto getUserById(@PathVariable Long id) throws ObjectNotFountException {
        return userService.getUserById(id);
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
