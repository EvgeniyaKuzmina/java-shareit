package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Builder
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) throws ConflictException {
        User user = UserMapper.toUser(userDto);
        try {
            log.info("Добавлен пользователь {}.", user);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Пользователь с таким email {} уже существует.", user.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    user.getEmail()));
        }
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) throws ObjectNotFountException, ConflictException {
        User updUser = getUserById(userId); // проверка, что пользователь с указанным id есть
        // обновляем данные
        Optional.ofNullable(userDto.getName()).ifPresent(updUser::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(updUser::setEmail);

        try {
            log.info("Добавлен пользователь {}.", updUser);
            return userRepository.save(updUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Пользователь с таким email {} уже существует.", updUser.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    updUser.getEmail()));
        }
        // return updUser;
    }

    @Override
    public void removeUser(Long id) throws ObjectNotFountException {
        getUserById(id); // проверка, что пользователь с указанным id есть
        log.warn("Пользователя с указанным id {} удалён", id);
        userRepository.deleteById(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) throws ObjectNotFountException {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(() -> {
            log.warn("Пользователя с указанным id {} нет", id);
            return new ObjectNotFountException("Пользователя с указанным id " + id + " нет");
        });

        log.warn("Пользователя с указанным id {} получен", id);
        return user.get();
    }
}
