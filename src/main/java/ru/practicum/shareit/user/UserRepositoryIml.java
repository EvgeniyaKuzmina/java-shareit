package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryIml implements UserRepository {

    private static long firstId = 1;
    private final Map<Long, User> users;

    @Override
    public User createUser(User user) {
        Long id;
        if (users.isEmpty()) {
            id = firstId;
        } else {
            id = nextId();
        }
        user.setId(id);
        users.put(id, user);
        log.info("UserRepositoryIml.createUser: Пользователь создан, id пользователя {} ", id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User updUser = users.get(user.getId());
        if (user.getEmail() != null) {
            updUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updUser.setName(user.getName());
        }
        log.info("UserRepositoryIml.updateUser: Пользователь c id {} обновлён", user.getId());
        return updUser;
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
        log.info("UserRepositoryIml.removeUser: Пользователь c id {} удалён", id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("UserRepositoryIml.getUserById: Пользователь c id {} получен", id);
        return Optional.of(users.get(id));
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("UserRepositoryIml.getAllUsers: Список всех пользователей получен");
        return users.values();
    }

    private Long nextId() {
        return ++firstId;
    }
}
