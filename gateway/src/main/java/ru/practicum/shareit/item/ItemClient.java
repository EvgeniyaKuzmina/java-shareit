package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // создание вещи
    public ResponseEntity<Object> createItem(ItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    // обновление данных о вещи
    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long id, Long userId) {
        return patch("/" + id, userId, itemDto);
    }

    //удаление вещи
    public ResponseEntity<Object> removeItem(Long id, Long userId) {
        return delete("/" + id, userId);
    }

    // получение вещи по id с комментариями
    public ResponseEntity<Object> getItemById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    // получение владельцем списка всех его вещей с комментариями. Эндпоинт GET items?from={from}&size={size}
    @GetMapping
    public ResponseEntity<Object> getAllItemByUserId(Long ownerId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?&from=" + from + "&size=" + size, ownerId, parameters);
    }

    // поиск вещи по части строки в названии или в описании. Эндпоинт GET items/search?text={text}&from={from}&size={size}
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameOrDescription(String text, Long userId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text=" + text + "&from=" + from + "&size=" + size, userId, parameters);
    }

    // добавление комментария к вещи после бронирования
    public ResponseEntity<Object> addNewComment(CommentDto commentDto, Long userId, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }


}
