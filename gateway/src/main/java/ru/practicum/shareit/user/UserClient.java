package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // создание пользователя
    public ResponseEntity<Object> createUser(UserDto userDto) throws ArgumentNotValidException {
        return post("", userDto);
    }

    // обновление пользователя
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        return patch("/" + id, userDto);
    }

    // удаление пользователя по id
    public ResponseEntity<Object>  removeUser(@PathVariable Long id) {
        return delete("" + id);
    }

    // получение пользователя по Id
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return get("/" + id);
    }

    // получение списка всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

}
