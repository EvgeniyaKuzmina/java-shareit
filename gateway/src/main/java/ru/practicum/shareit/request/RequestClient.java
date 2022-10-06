package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // создание нового запроса вещи.
    public ResponseEntity<Object> createRequest(RequestDto itemRequestDto, Long requesterId) {
        return post("", requesterId, itemRequestDto);
    }

    //Получение списка всех своих запросов вместе с данными об ответах на них. Эндпоинт GET /request?from={from}&size={size}

    public ResponseEntity<Object> getAllRequestsByUserId(Long requesterId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from=" + from + "&size=" + size, requesterId, parameters);
    }

    // Получение списка запросов, созданных другими пользователями. Эндпоинт GET /request/all?from={from}&size={size}
    public ResponseEntity<Object> getAllRequestsCreatedAnotherUsers(Long requesterId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from=" + from + "&size=" + size, requesterId, parameters);

    }

    // Получение данных об одном конкретном запросе вместе с данными об ответах на него. Эндпоинт GET /request/{requestId}
    public ResponseEntity<Object> getRequestById(Long requesterId, Long id) {
        return get("/" + id, requesterId);
    }


}
