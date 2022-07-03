package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;

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
    public User createUser(@Valid @RequestBody User user) throws ValidationException, ArgumentNotValidException {
        if (user.getEmail() == null) {
            throw new ArgumentNotValidException("Не указан email пользователя");
        }
        return userService.createUser(user);
    }

    // обновление пользователя
    @PatchMapping(value = {"/{id}"})
    public User updateUser(@Valid @RequestBody User user, @PathVariable Long id) throws ConflictException, ObjectNotFountException {
        return userService.updateUser(user, id);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public void removeUser(@PathVariable Long id) throws ObjectNotFountException {
        userService.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    @ResponseBody
    public User getUserById(@PathVariable Long id) throws ObjectNotFountException {
        return userService.getUserById(id);
    }

    // получение списка всех пользователей
    @GetMapping
    @ResponseBody
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
