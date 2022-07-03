package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    // создание пользователя
    User createUser(User user);

    // обновление пользователя
    User updateUser(User user);


    // удаление пользователя по id
    void removeUser(Long id);

    // получение пользователя по Id
    Optional<User> getUserById(Long id);

    // получение всех пользователей
    Collection<User> getAllUsers();

    Map<Long, User> getUsers();
}
