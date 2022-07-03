package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
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
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.updateUser(user);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public void removeUser(@PathVariable Long id) throws ValidationException {
        userService.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    @ResponseBody
    public User getUserById(@PathVariable Long id) throws ValidationException {
        return userService.getUserById(id);
    }

    // получение списка всех пользователей
    @GetMapping
    @ResponseBody
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
