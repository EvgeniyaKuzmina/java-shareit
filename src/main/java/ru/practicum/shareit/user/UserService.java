package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface UserService {

    // создание пользователя
    User createUser(User user);

    //обновление пользователя
    User updateUser(User user, Long id) throws ObjectNotFountException;

    // удаление пользователя по id
    void removeUser(Long id) throws ObjectNotFountException;

    // получение списка всех пользователей
    Collection<User> getAllUsers();

    // получение пользователя по id
    User getUserById(Long id) throws ObjectNotFountException;
}
