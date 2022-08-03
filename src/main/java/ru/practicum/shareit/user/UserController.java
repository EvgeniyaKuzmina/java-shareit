package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

/**
 * контроллер для работы с пользователями
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    // создание пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) throws ArgumentNotValidException {
        if (userDto.getEmail() == null) {
            throw new ArgumentNotValidException("Не указан email пользователя");
        }
        User user  = UserMapper.toUser(userDto);
        userDto = UserMapper.toUserDto(userServiceImpl.createUser(user));
        return userDto;
    }

    // обновление пользователя
    @PatchMapping(value = {"/{id}"})
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) throws ObjectNotFountException {
        User user = userServiceImpl.updateUser(UserMapper.toUser(userDto), id);
        return UserMapper.toUserDto(user);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable Long id) throws ObjectNotFountException {
        userServiceImpl.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    public UserDto getUserById(@PathVariable Long id) throws ObjectNotFountException {
        User user = userServiceImpl.getUserById(id);
        return UserMapper.toUserDto(user);
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> allUsersDto = new ArrayList<>();
        userServiceImpl.getAllUsers().forEach(u -> allUsersDto.add(UserMapper.toUserDto(u)));
        return allUsersDto;
    }
}
