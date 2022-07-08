package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) throws ValidationException {
        List<User> usersList = new ArrayList<>(userRepository.getUsers().values());
        for (User u : usersList) {
            if (u.getEmail().equals(userDto.getEmail())) {
                log.error("UserService.createUser: Пользователь с таким email {} уже существует ", userDto.getEmail());
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.createUser(userDto);
    }

    public UserDto updateUser(UserDto userDto, Long id) throws ConflictException, ObjectNotFountException {
        getUserById(id); // проверка, что пользователь с указанным id есть

        List<User> usersList = new ArrayList<>(userRepository.getUsers().values());
        for (User u : usersList) {
            if (u.getEmail().equals(userDto.getEmail()) && !u.getId().equals(id)) {
                log.error("UserService.updateUser: Пользователь с таким email {} уже существует ", userDto.getEmail());
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.updateUser(userDto, id);
    }

    public void removeUser(Long id) throws ObjectNotFountException {
        getUserById(id); // проверка, что пользователь с указанным id есть
        userRepository.removeUser(id);
    }

    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(Long id) throws ObjectNotFountException {
        if (!userRepository.getUsers().containsKey(id)) {
            log.warn("Пользователя с указанным id {} нет", id);
            throw new ObjectNotFountException("Пользователя с указанным id " + id + " нет");
        }
        return userRepository.getUserById(id);
    }
}
