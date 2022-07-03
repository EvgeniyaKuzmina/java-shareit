package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ObjectNotFountException;

import java.util.Collection;
import java.util.Map;

public interface UserRepository {

    // создание пользователя
    User createUser(User user);

    // обновление пользователя
    User updateUser(User user, Long id);

    // удаление пользователя по id
    void removeUser(Long id);

    // получение пользователя по Id
    User getUserById(Long id) throws ObjectNotFountException;

    // получение всех пользователей
    Collection<User> getAllUsers();

    Map<Long, User> getUsers();
}
