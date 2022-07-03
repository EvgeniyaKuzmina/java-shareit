package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;

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

    public User updateUser(User user) throws ValidationException {
        getUserById(user.getId()); // проверка, что пользователь с указанным id есть

        List<User> usersList = new ArrayList<>(userRepository.getUsers().values());
        for (User u : usersList) {
            if (u.getEmail().equals(user.getEmail()) & !Objects.equals(u.getId(), user.getId())) {
                log.error("UserService.updateUser: Пользователь с таким email {} уже существует ", user.getEmail());
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.updateUser(user);
    }

    public void removeUser(Long id) throws ValidationException {
        getUserById(id); // проверка, что пользователь с указанным id есть
        userRepository.removeUser(id);
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(Long id) throws ValidationException {
        Optional<User> user = userRepository.getUserById(id);
        user.orElseThrow(() -> {
            log.error("UserService.getUserById: Пользователя с таким id {} нет ", id);
            return new ValidationException("Пользователя с таким id нет");
        });

        return user.get();
    }
}
