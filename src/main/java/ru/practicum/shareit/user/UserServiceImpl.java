package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);

    }

    @Override
    public User updateUser(User user, Long id) throws ObjectNotFountException {
        User updUser = getUserById(id); // проверка, что пользователь с указанным id есть

        // обновляем данные
        Optional.ofNullable(user.getName()).ifPresent(updUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(updUser::setEmail);

        userRepository.save(updUser);
        return updUser;
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
