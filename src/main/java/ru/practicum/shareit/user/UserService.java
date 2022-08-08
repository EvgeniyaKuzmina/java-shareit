package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    // создание пользователя
    User createUser(UserDto UserDto);

    //обновление пользователя
    User updateUser(UserDto UserDto, Long id) throws ObjectNotFountException;

    // удаление пользователя по id
    void removeUser(Long id) throws ObjectNotFountException;

    // получение списка всех пользователей
    Collection<User> getAllUsers();

    // получение пользователя по id
    User getUserById(Long id) throws ObjectNotFountException;
}
