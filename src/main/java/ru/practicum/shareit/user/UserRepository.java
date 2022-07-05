package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Map;

public interface UserRepository {

    // создание пользователя
    UserDto createUser(UserDto userDto);

    // обновление пользователя
    UserDto updateUser(UserDto userDto, Long id);

    // удаление пользователя по id
    void removeUser(Long id);

    // получение пользователя по Id
    UserDto getUserById(Long id) throws ObjectNotFountException;

    // получение всех пользователей
    Collection<UserDto> getAllUsers();

    Map<Long, User> getUsers();
}
