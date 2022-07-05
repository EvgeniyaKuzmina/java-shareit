package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryIml implements UserRepository {

    private static long firstId = 1;
    private final Map<Long, User> users;

    public UserDto createUser(UserDto userDto) {
        Long id;
        if (users.isEmpty()) {
            id = firstId;
        } else {
            id = nextId();
        }
        userDto.setId(id);
        users.put(id, UserMapper.toUser(userDto));
        log.info("UserRepositoryIml.createUser: Пользователь создан, id пользователя {} ", id);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User updUser = users.get(id);
        if (userDto.getEmail() != null) {
            updUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            updUser.setName(userDto.getName());
        }
        log.info("UserRepositoryIml.updateUser: Пользователь c id {} обновлён", userDto.getId());
        return UserMapper.toUserDto(updUser);
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
        log.info("UserRepositoryIml.removeUser: Пользователь c id {} удалён", id);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("UserRepositoryIml.getUserById: Пользователь c id {} получен", id);
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDto = new ArrayList<>();
        users.values().forEach(u -> usersDto.add(UserMapper.toUserDto(u)));
        log.info("UserRepositoryIml.getAllUsers: Список всех пользователей получен");
        return usersDto;
    }

    private Long nextId() {
        return ++firstId;
    }
}
