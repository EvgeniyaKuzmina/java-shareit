package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) throws ValidationException {
        List<User> usersList = new ArrayList<>(userRepository.getUsers().values());
        for (User u : usersList) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("UserService.createUser: Пользователь с таким email {} уже существует ", user.getEmail());
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.createUser(user);
    }

    public User updateUser(User user, Long id) throws ConflictException, ObjectNotFountException {
        getUserById(id); // проверка, что пользователь с указанным id есть

        List<User> usersList = new ArrayList<>(userRepository.getUsers().values());
        for (User u : usersList) {
            if (u.getEmail().equals(user.getEmail()) && !u.getId().equals(id)) {
                log.error("UserService.updateUser: Пользователь с таким email {} уже существует ", user.getEmail());
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.updateUser(user, id);
    }

    public void removeUser(Long id) throws ObjectNotFountException {
        getUserById(id); // проверка, что пользователь с указанным id есть
        userRepository.removeUser(id);
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(Long id) throws ObjectNotFountException {
        if (!userRepository.getUsers().containsKey(id)) {
            log.warn("Пользователя с указанным id {} нет", id);
            throw new ObjectNotFountException("Пользователя с указанным id " + id + " нет");
        }
        return userRepository.getUserById(id);
    }
}
