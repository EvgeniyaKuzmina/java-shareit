package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    // создание пользователя
    User createUser(UserDto userDto);

    //обновление пользователя
    User updateUser(UserDto userDto, Long id);

    // удаление пользователя по id
    void removeUser(Long id);

    // получение списка всех пользователей
    Collection<User> getAllUsers();

    // получение пользователя по id
    User getUserById(Long id);
}
